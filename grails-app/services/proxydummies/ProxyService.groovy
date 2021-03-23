package proxydummies

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import io.micronaut.http.HttpRequest
import org.apache.commons.lang3.StringEscapeUtils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import proxydummies.abstracts.BaseService
import proxydummies.exceptions.DummiesException
import proxydummies.filters.FilterResult
import proxydummies.filters.RuleFilter
import proxydummies.utilities.DummiesMessageCode

import javax.servlet.http.HttpServletRequest
import java.util.logging.Filter

@Transactional
class ProxyService extends BaseService{

    SystemConfigsService systemConfigsService
    FileServicesService fileServicesService

    final static String PROXY_DUMMIES_URI_PREFIX = "/proxyDummies"
    final static String DUMMIES_NAME_PREFIX = "DUMMIES"
    final static String DUMMIES_NAME_EXT = ".xml"

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

    Rule evalRules(List<Rule> candidates, XmlNavigator soapXmlRequest){
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
                        soapXmlRequest.reset()
                        evalExpression = Eval.me( "\$requestXml", soapXmlRequest, candidate.requestCondition )
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

    void createDummy(String body, String fileName){
        Boolean saveResponseConfig = systemConfigsService.getSaveResponse()
        if(saveResponseConfig){
            String saveResponseFolder = systemConfigsService.getConfigValueByKey( SystemConfigsService.CONFIG_KEY_SAVE_RESPONSE_FOLDER )

            fileServicesService.saveDataIntoFile( body, saveResponseFolder, fileName )
        }
    }

    String generateDummyNameFromUri( String uriName ){
        generateDummyName( uriName.replaceAll( "/", ".") )
    }

    String generateDummyName(String name ){
        String expressionName = ""

        if( !systemConfigsService.getOverrideSaveResponses() ){
            def expression = systemConfigsService.getOverrideSaveResponsesExpression()
            expressionName= Eval.me( expression )
        }

        DUMMIES_NAME_PREFIX + expressionName + name + DUMMIES_NAME_EXT
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

        plainRule.sourceType = Rule.SourceType.DATABASE

        (plainRule as JSON).toString()
    }

    Rule importRule(String pUri,
                    String pData,
                    Boolean pActive,
                    String pDescription,
                    Boolean pRequestConditionActive,
                    String pRequestCondition ){

        Rule maximumPriorityRule = Rule.findAllByUri(pUri, [sort: 'priority', order: 'desc', limit: 1])?.first()
        Integer newMaximumPriority = ( maximumPriorityRule ? (maximumPriorityRule.priority+1) : 1 )

        saveRule(pUri, newMaximumPriority, Rule.SourceType.DATABASE, pData, pActive, pDescription, pRequestConditionActive, pRequestCondition)
    }

}
