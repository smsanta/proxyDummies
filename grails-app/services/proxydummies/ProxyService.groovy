package proxydummies

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Restrictions
import proxydummies.abstracts.BaseService
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
    final static String DUMMIES_NAME_EXT = ".xml"

    FilterResult searchRule( RuleFilter filter){
        filter.withCriteria {
            filter.uri ? add(Restrictions.ilike('uri', filter.uri, MatchMode.ANYWHERE)) : null
            filter.active != null ?  add(Restrictions.eq("active", filter.active)) : null

            order('uri')
            order('active')
            order('priority')

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
            saveRule.with {
                uri = pUri
                priority = pPriority
                sourceType = pSourceType
                data = pData
                active = pActive
                description = pDescription
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

    Rule evalRules( List<Rule> candidates ){
        /*Rule priorityCandidate = null
        candidates.each { Rule candidate ->
            if( candidate.active ){
                priorityCandidate = candidate
            }
        }
        priorityCandidate*/
        candidates.first()
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
        DUMMIES_NAME_PREFIX + name + DUMMIES_NAME_EXT
    }
}
