package proxydummies


import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.apache.commons.lang3.StringEscapeUtils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import proxydummies.abstracts.BaseService
import proxydummies.abstracts.RequestObjectNavigator
import proxydummies.exceptions.DummiesException
import proxydummies.filters.FilterResult
import proxydummies.filters.RuleFilter
import proxydummies.utilities.DummiesMessageCode

@Transactional
class ProxyService extends BaseService{

    SystemConfigsService systemConfigsService
    FileServicesService fileServicesService

    final static String PROXY_DUMMIES_URI_PREFIX = "/proxyDummies"
    final static String DUMMIES_NAME_PREFIX = "DUMMIES"
    final static String DUMMIES_RESPONSE_NAME_EXT = ".xml"
    final static String DUMMIES_IMPORT_NAME_EXT = ".json"

    FilterResult searchRule( RuleFilter filter ){
        filter.withCriteria {
            filter.id != null ?  add(Restrictions.eq("id", filter.id)) : null
            filter.uri ? add(Restrictions.ilike('uri', filter.uri, MatchMode.ANYWHERE)) : null
            filter.active != null ?  add(Restrictions.eq("active", filter.active)) : null

            order('uri')
            order('active')
            order('priority', 'DESC')
        }
    }

    List<Rule> getActiveRules( String uri ){
        FilterResult result = searchRule( RuleFilter.newInstance( [ uri: uri, active: true ]) )
        result.results
    }

    Rule saveRule( String pUri,
        Integer pPriority,
        Rule.SourceType pSourceType,
        String pData,
        Boolean pActive,
        String pDescription,
        Boolean pRequestConditionActive,
        String pRequestCondition,
        Boolean pIsJson,
        Long id = null ){
        handle{
            if ( Rule.countByUriAndPriorityAndIdNotEqual( pUri, pPriority, id ) > 0 ){
                throw new DummiesException( DummiesMessageCode.SAVE_DUMMY_ALREADY_EXISTS )
            }

            Rule saveRule = Rule.newInstance()

            if( id ){
                saveRule = Rule.findById( id )

                if(!saveRule){
                    throw new DummiesException( DummiesMessageCode.RULE_COULD_NOT_BE_FOUND )
                }
            }

            String ruleData = pData
            if( pSourceType == Rule.SourceType.DATABASE ){
                ruleData = StringEscapeUtils.unescapeXml( ruleData )
            }

            saveRule.with {
                uri = pUri
                priority = pPriority
                sourceType = pSourceType
                data = ruleData
                active = pActive
                description = pDescription
                requestConditionActive = (pRequestConditionActive ?: false)
                requestCondition = (pRequestConditionActive ? pRequestCondition : "")
                isJson = (pIsJson ?: false)
            }

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

    RequestObjectNavigator getRequestObjectNavigator(String plainRequestObject, Boolean isJson ){
        isJson ? JsonNavigator.newInstance( plainRequestObject ) : XmlNavigator.newInstance( plainRequestObject )
    }

    Rule evalRules(List<Rule> candidates, String requestBody){
        Rule priorityCandidate = null

        List<Rule> sortedCandidates = candidates.sort { a, b ->
            a.priority == b.priority ? 0 : a.priority > b.priority ? -1 : 1
        }

        info( "Starting Evaluating candidates: ")
        info( sortedCandidates.toList() )

        for (Rule candidate in sortedCandidates){
            info( "Evaluating candidate: -> $candidate")
            if( candidate.active ){
                if( candidate.requestConditionActive ){

                    Boolean evalExpression = false
                    try{
                        def requestObject = getRequestObjectNavigator( requestBody, candidate.isJson )
                        evalExpression = Eval.me( "\$request", requestObject, candidate.requestCondition )
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
        str.replaceAll( PROXY_DUMMIES_URI_PREFIX, "")
    }

    void saveResponse(String uriName, String body){
        Boolean saveResponseConfig = systemConfigsService.getSaveResponse()
        String saveResponseFolder = systemConfigsService.getConfigValueByKey( SystemConfigsService.CONFIG_KEY_SAVE_RESPONSE_FOLDER )

        if( saveResponseConfig ){
            def fileName = generateDummyNameFromUri( uriName, DUMMIES_RESPONSE_NAME_EXT)
            fileServicesService.saveDataIntoFile( body, saveResponseFolder, fileName )
        }

        Boolean autoGenerateImportDummyFromResponses = systemConfigsService.getAutoGenerateImportDummyFromResponses()
        if( autoGenerateImportDummyFromResponses ){
            def fileName = generateDummyNameFromUri( uriName, DUMMIES_IMPORT_NAME_EXT)
            def importBody = generateImportRuleFromResponse( uriName, body)
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

    Rule importRule(String pUri,
                    String pData,
                    String pDescription,
                    Boolean pRequestConditionActive,
                    String pRequestCondition,
                    Boolean pIsJson){

        Integer newMaximumPriority = getNewMaximumPriorityRule( pUri )

        saveRule(pUri, newMaximumPriority, Rule.SourceType.DATABASE, pData, false, pDescription, pRequestConditionActive, pRequestCondition, pIsJson)
    }

    String generateImportRuleFromResponse(pUri, pData){
        def newRuleMap = [
            uri: pUri,
            description: "Autogenerated rule for: $pUri",
            data: pData,
            requestConditionActive: false,
            requestCondition: ""
        ]

        (newRuleMap as JSON).toString()
    }

    Integer getNewMaximumPriorityRule( String pUri ){
        List<Rule> maximumPriorityRule = Rule.findAllByUri(pUri, [sort: 'priority', order: 'desc', limit: 1])
        ( !maximumPriorityRule.isEmpty() ? ( maximumPriorityRule.first().priority + 1 ) : 1 )
    }

}
