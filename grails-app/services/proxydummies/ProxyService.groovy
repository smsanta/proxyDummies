package proxydummies

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import io.micronaut.http.HttpRequest
import org.apache.commons.lang3.StringEscapeUtils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import proxydummies.Rule.HttpMethod
import proxydummies.Rule.ServiceType
import proxydummies.abstracts.BaseService
import proxydummies.exceptions.DummiesException
import proxydummies.filters.EnvironmentFilter
import proxydummies.filters.FilterResult
import proxydummies.filters.RuleFilter
import proxydummies.utilities.DummiesMessageCode

import javax.servlet.http.HttpServletRequest
import java.util.logging.Filter

@Transactional
class ProxyService extends BaseService{

    SystemConfigsService systemConfigsService
    FileServicesService fileServicesService

    private def _serviceObject = null

    final static String PROXY_DUMMIES_APPLICATION_CONFIG_PREFIX = "proxy-dummies.uri-prefix"
    final static String DUMMIES_NAME_PREFIX = "DUMMIES"
    final static String DUMMIES_RESPONSE_NAME_EXT = ".xml"
    final static String DUMMIES_IMPORT_NAME_EXT = ".json"

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
        RuleFilter ruleFilter = RuleFilter.newInstance( [ uri: uri, method: method, active: true ] )
        FilterResult result = searchRule( ruleFilter )
        result.results
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
                if ( !_serviceObject ){
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

    String purgeProxyDummiesPrefix( String str ){
        String proxyDummiesPrefix = getApplicationConfigProperty( PROXY_DUMMIES_APPLICATION_CONFIG_PREFIX )
        str.replaceAll( proxyDummiesPrefix, "")
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
            pResponseExtraHeaders
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
            throw new DummiesException( DummiesMessageCode.Environment_COULD_NOT_BE_FOUND )
        }

        if( systemConfigsService.getDefaultEnvironmentId() == deleteEnvironment.id ){
            throw new DummiesException( DummiesMessageCode.ENVIRONMENT_CANT_DELETE_DEFAULT )
        }

        if( Rule.countByEnvironment( deleteEnvironment ) > 0 ){
            throw new DummiesException( DummiesMessageCode.ENVIRONMENT_CANT_DELETE_WITH_ASOCIATED_RULE )
        }

        deleteEnvironment.delete()
    }

}
