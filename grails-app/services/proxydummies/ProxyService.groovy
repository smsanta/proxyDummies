package proxydummies

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import proxydummies.Rule.HttpMethod
import proxydummies.Rule.ServiceType
import proxydummies.abstracts.BaseService
import proxydummies.exceptions.DummiesException
import proxydummies.filters.EnvironmentFilter
import proxydummies.filters.FilterResult
import proxydummies.filters.RequestLogFilter
import proxydummies.filters.RuleFilter
import proxydummies.utilities.DummiesMessageCode

@Transactional
class ProxyService extends BaseService{

    SystemConfigsService systemConfigsService
    FileServicesService fileServicesService
    def executorService

    private def _serviceObject = null

    final static String PROXY_DUMMIES_APPLICATION_CONFIG_PREFIX = "proxy-dummies.uri-prefix"
    final static String DUMMIES_NAME_PREFIX = "DUMMIES"
    final static String DUMMIES_RESPONSE_NAME_EXT = ".xml"
    final static String DUMMIES_IMPORT_NAME_EXT = ".json"
    final static String CONTENT_TYPE_HEADER_KEY = 'Content-Type'

    final static def DEFAULT_EXTENDED_HEADERS_SOAP = 'text/xml'
    final static def DEFAULT_EXTENDED_HEADERS_JSON = 'application/json'

    FilterResult searchRule( RuleFilter filter ){
        filter.withCriteria {
            filter.id ?  add(Restrictions.eq("id", filter.id ) ) : null
            filter.uri ? add(Restrictions.ilike('uri', filter.uri, MatchMode.ANYWHERE ) ) : null
            filter.method ? add(Restrictions.eq("method", filter.method ) ) : null
            filter.active ? add(Restrictions.eq("active", filter.active ) ) : null

            order('uri')
            order('active')
            order('priority', 'DESC')
        }
    }

    List<Rule> getActiveRules( String uri, String method ){
        Rule.findAllByUriAndMethodAndActive( uri, method, true, [sort: "priority"] )
    }

    Rule saveRule(String pUri,
                  Integer pPriority,
                  Rule.SourceType pSourceType,
                  String pData,
                  Boolean pActive,
                  String pDescription,
                  Boolean pRequestConditionActive,
                  String pRequestCondition,
                  Rule.HttpMethod pMethod,
                  Rule.ServiceType serviceType,
                  Integer responseStatus,
                  String responseExtraHeaders,
                  Boolean includeDefaultContentType,
                  Long id = null
    ){
        handle{
            if ( Rule.countByUriAndMethodAndPriorityAndIdNotEqual( pUri, pMethod, pPriority, id ) > 0 ){
                throw new DummiesException( DummiesMessageCode.SAVE_DUMMY_ALREADY_EXISTS )
            }

            Rule saveRule = Rule.newInstance()

            if( id ){
                saveRule = Rule.findById( id )

                if(!saveRule){
                    throw new DummiesException( DummiesMessageCode.RULE_COULD_NOT_BE_FOUND )
                }
            }

            //TODO CHECK DECODE DATA.
            String ruleData = URLDecoder.decode( pData )

            if( includeDefaultContentType ){
                def extendedExtraHeaders = responseExtraHeaders ? Eval.me( responseExtraHeaders ) : [:]
                extendedExtraHeaders[CONTENT_TYPE_HEADER_KEY] = Rule.ServiceType.SOAP == serviceType ? DEFAULT_EXTENDED_HEADERS_SOAP : DEFAULT_EXTENDED_HEADERS_JSON

                responseExtraHeaders = extendedExtraHeaders.inspect()
            }

            saveRule.safeSetter([
                uri: pUri,
                priority: pPriority,
                sourceType: pSourceType,
                data: ruleData,
                active: pActive,
                description: pDescription,
                requestConditionActive: (pRequestConditionActive ?: false),
                requestCondition: (pRequestConditionActive ? pRequestCondition : ""),
                method: pMethod,
                serviceType: serviceType,
                responseStatus: responseStatus,
                responseExtraHeaders: (responseExtraHeaders ?: "")
            ])

            saveRule.save( flush: true, failOnError: true )
        }
    }

    Rule changeRuleState( Long id, Boolean newState ){
        Rule updateRule = Rule.findById( id )

        if( !updateRule ){
            throw new DummiesException( DummiesMessageCode.RULE_COULD_NOT_BE_FOUND )
        }

        updateRule.active = newState
        updateRule.save( flush: true )
        updateRule
    }

    void deleteRule( Long id ){
        Rule deleteRule = Rule.findById( id )

        if( !deleteRule ){
            throw new DummiesException( DummiesMessageCode.RULE_COULD_NOT_BE_FOUND )
        }

        deleteRule.delete()
    }

    Rule evalRules( List<Rule> candidates, String requestBody, queryParams, String requestUri ){
        Rule priorityCandidate = null

        List<Rule> sortedCandidates = candidates.sort { a, b ->
            a.priority == b.priority ? 0 : a.priority > b.priority ? -1 : 1
        }

        info( "Starting Evaluating candidates: ")
        info( sortedCandidates )

        for (Rule candidate in sortedCandidates){
            info( "Evaluating candidate: -> $candidate")
            if( candidate.active ){
                if( candidate.requestConditionActive ){
                    def payloadObject = getRequestBodyObject( requestBody, candidate.serviceType  )
                    Boolean evalExpression = false
                    try{
                        def expressionData = [ payload: payloadObject, params: queryParams ]
                        evalExpression = Eval.me( "\$request", expressionData, candidate.requestCondition )
                    }catch(NoSuchElementException e){
                        info("NoSuchElementException: Returning false on current Rule.")
                    }

                    if( evalExpression ){
                        info("Selected Rule -> $candidate")
                        priorityCandidate = candidate
                        break
                    }

                    continue
                }

                info("Selected Rule -> $candidate")
                priorityCandidate = candidate
                break

            }
        }

        priorityCandidate
    }

    def getRequestBodyObject(String requestBody, Rule.ServiceType serviceType) {
        switch (serviceType){
            case Rule.ServiceType.REST:
                if ( !_serviceObject && requestBody ){
                    _serviceObject = requestBody as JSON
                }
                break
            case Rule.ServiceType.SOAP:
                if ( !_serviceObject ){
                    _serviceObject = XmlNavigator.newInstance( requestBody )
                }
                _serviceObject.reset()
                break
            default:
                throw new DummiesException( DummiesMessageCode.RULE_INVALID_SERVICE_TYPE )
        }

        _serviceObject
    }

    String loadDummy(Rule rule){
        String dummy = null

        switch ( rule.sourceType ){
            case Rule.SourceType.DATABASE:
                dummy = rule.data
                break
            case Rule.SourceType.FILE:
                dummy = fileServicesService.loadFileData( rule.data )
                break
            default: throw new DummiesException( DummiesMessageCode.DUMMY_COULD_NOT_BE_LOADED )
        }

        dummy
    }

    String purgeProxyDummiesPrefix( String str, Environment environment = null){
        String proxyDummiesPrefix = getApplicationConfigProperty( PROXY_DUMMIES_APPLICATION_CONFIG_PREFIX )
        if( environment ){
            proxyDummiesPrefix += "/${environment.uriPrefix}"
        }

        info( "Purging prefix -$proxyDummiesPrefix- from -$str-." )
        String purgedUri = str.replaceAll( proxyDummiesPrefix, "")
        info( "Final uri is: $purgedUri" )
        purgedUri

    }

    void saveResponse(String uriName, String body, String method){
        Boolean saveResponseConfig = systemConfigsService.getSaveResponse()
        String saveResponseFolder = systemConfigsService.getConfigurationValueByKey( SystemConfigsService.CONFIG_KEY_SAVE_RESPONSE_FOLDER )

        if( saveResponseConfig ){
            def fileName = generateDummyNameFromUri( uriName, DUMMIES_RESPONSE_NAME_EXT)
            fileServicesService.saveDataIntoFile( body, saveResponseFolder, fileName )
        }

        Boolean autoGenerateImportDummyFromResponses = systemConfigsService.getAutoGenerateImportDummyFromResponses()
        if( autoGenerateImportDummyFromResponses ){
            def fileName = generateDummyNameFromUri( uriName, DUMMIES_IMPORT_NAME_EXT)
            def importBody = generateImportRuleFromResponse( uriName, body, method )
            fileServicesService.saveDataIntoFile( importBody, saveResponseFolder, fileName )
        }
    }

    String generateDummyNameFromUri( String uriName, String ext ){
        generateDummyName( uriName.replaceAll( "/", "."), ext )
    }

    String generateDummyName(String name, String ext ){
        String expressionName = ""

        if( !systemConfigsService.getOverrideSaveResponses() ){
            def expression = systemConfigsService.getOverrideSaveResponsesExpression()
            expressionName= Eval.me( expression )
        }

        DUMMIES_NAME_PREFIX + expressionName + name + ext
    }

    String exportRule(Long id){
        FilterResult ruleFilterResult = searchRule( RuleFilter.newInstance( [id: id] ))
        Rule ruleToExport = ruleFilterResult.results.first()

        if( !ruleToExport ){
            throw new DummiesException( DummiesMessageCode.RULE_COULD_NOT_BE_FOUND )
        }

        def data = loadDummy( ruleToExport )

        def plainRule = ruleToExport.toMapObject()
        plainRule.data = data
        plainRule.sourceType = Rule.SourceType.DATABASE.name()

        (plainRule as JSON).toString()
    }

    Rule importRule(
        String pUri,
        String pData,
        String pDescription,
        Boolean pRequestConditionActive,
        String pRequestCondition,
        HttpMethod pMethod,
        ServiceType pServiceType,
        Integer pResponseStatus,
        String pResponseExtraHeaders
    ){

        Integer newMaximumPriority = getNewMaximumPriorityRule( pUri )

        saveRule(
            pUri,
            newMaximumPriority,
            Rule.SourceType.DATABASE,
            pData,
            false,
            pDescription,
            pRequestConditionActive,
            pRequestCondition,
            pMethod,
            (pServiceType ?: ServiceType.SOAP),
            (pResponseStatus ?: 200),
            pResponseExtraHeaders,
            false
        )
    }

    String generateImportRuleFromResponse(pUri, pData, pMethod){
        def newRuleMap = [
            uri: pUri,
            responseStatus: 200,
            method: pMethod,
            serviceType: systemConfigsService.getAutoGenerateImportDummyDefaultServiceType(),
            description: "Autogenerated rule for: $pUri",
            requestConditionActive: false,
            requestCondition: "",
            data: pData
        ]

        (newRuleMap as JSON).toString()
    }

    Integer getNewMaximumPriorityRule( String pUri ){
        List<Rule> maximumPriorityRule = Rule.findAllByUri(pUri, [sort: 'priority', order: 'desc', limit: 1])
        ( !maximumPriorityRule.isEmpty() ? ( maximumPriorityRule.first().priority + 1 ) : 1 )
    }

    FilterResult searchEnvironment(EnvironmentFilter filter ){
        handle{
            filter.withCriteria {
                filter.id != null ? add(Restrictions.eq("id", filter.id)) : null
                filter.name ? add(Restrictions.ilike('name', filter.uri, MatchMode.ANYWHERE)) : null
                filter.url ? add(Restrictions.ilike('url', filter.uri, MatchMode.ANYWHERE)) : null
                filter.uriPrefix ? add(Restrictions.ilike('uriPrefix', filter.uri, MatchMode.ANYWHERE)) : null

                order('name')
            }
        }
    }

    Environment saveEnvironment( String pName, String pUrl, String pUriPrefix, Long id = null ){
        handle{
            Environment saveEnvironment = Environment.newInstance()

            if( id ){

                saveEnvironment = Environment.findById( id )

                if(!saveEnvironment){
                    throw new DummiesException( DummiesMessageCode.ENVIRONMENT_COULD_NOT_BE_FOUND )
                }
            }

            saveEnvironment.safeSetter([
                name: pName,
                url: pUrl,
                uriPrefix: pUriPrefix
            ])

            saveEnvironment.save( flush: true, failOnError: true )
        }
    }

    void deleteEnvironment( Long id ){
        Environment deleteEnvironment = Environment.findById( id )

        if( !deleteEnvironment ){
            throw new DummiesException( DummiesMessageCode.ENVIRONMENT_COULD_NOT_BE_FOUND )
        }

        if( systemConfigsService.getDefaultEnvironmentId() == deleteEnvironment.id ){
            throw new DummiesException( DummiesMessageCode.ENVIRONMENT_CANT_DELETE_DEFAULT )
        }

        deleteEnvironment.delete()
    }

    void registerRequestLog(
        Date date,
        String uri,
        Boolean forwarded,
        String urlDestination,
        String requestHeaders,
        String requestBody,
        String requestType,
        Integer responseStatus,
        String responseHeaders,
        String responseBody,
        Rule rule
    ){
        executorService.execute{
            RequestLog newEntry = RequestLog.newInstance().safeSetter([
                uri: uri,
                eventDate: date,
                forwarded: forwarded,
                urlDestination: urlDestination,
                requestHeaders: requestHeaders,
                requestBody: requestBody,
                requestType: requestType,
                responseStatus: responseStatus,
                responseHeaders: responseHeaders,
                zResponseBody: responseBody,
                rule: rule
            ])

            newEntry.save( flush: true, failOnError: true )
        }
    }

    FilterResult searchRequestLogs(RequestLogFilter filter){
        filter.withCriteria ({
            order("eventDate", "desc")
        }, [max: filter.maxResults])
    }

    void emptyRequestLogs(){
        Date today = Date.newInstance().clearTime()

        info( "Clearing request logs older than: ${today}" )
        def resp = RequestLog.executeUpdate( "DELETE FROM RequestLog WHERE eventDate < :todayDate", [todayDate: today] )
        info( "$resp items where cleared from db." )
    }

}
