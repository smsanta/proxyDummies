package proxydummies

import grails.gorm.transactions.Transactional
import proxydummies.abstracts.BaseService
import proxydummies.exceptions.DummiesException
import proxydummies.utilities.DummiesMessageCode

@Transactional
class SystemConfigsService extends BaseService{

    final static String CONFIG_KEY_GLOBAL_REDIRECT_URL = "globalRedirectUrl"
    final static String CONFIG_KEY_ENABLE_GLOBAL_REDIRECT_URL = "enableGlobalRedirectUrl"
    final static String CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER = "proxyDummiesHome"
    final static String CONFIG_KEY_SAVE_RESPONSE = "saveResponses"
    final static String CONFIG_KEY_SAVE_RESPONSE_FOLDER = "saveResponsesFolder"
    final static String CONFIG_KEY_OVERRIDE_SAVE_RESPONSE = "overrideSaveResponses"
    final static String CONFIG_KEY_OVERRIDE_SAVE_RESPONSE_EXPRESSION = "overrideSaveResponsesExpression"
    final static String CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_FROM_RESPONSES = "autoGenerateImportDummyFromResponses"


    static List<String> getAllConfigurationKeys(){
        [
            CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER,
            CONFIG_KEY_SAVE_RESPONSE_FOLDER,
            CONFIG_KEY_GLOBAL_REDIRECT_URL,
            CONFIG_KEY_ENABLE_GLOBAL_REDIRECT_URL,
            CONFIG_KEY_SAVE_RESPONSE,
            CONFIG_KEY_OVERRIDE_SAVE_RESPONSE,
            CONFIG_KEY_OVERRIDE_SAVE_RESPONSE_EXPRESSION,
            CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_FROM_RESPONSES
        ]
    }

    Configuration createNewConfig( String key, String value, String description, String title ){
        Configuration newConfig = getConfigByKey( key )

        if( !newConfig ){
            newConfig = Configuration.newInstance()
            newConfig.key = key
        }

        newConfig.value = value
        newConfig.description = description
        newConfig.title = title

        newConfig.save( flush: true )

        newConfig
    }

    Configuration updateConfig( String key, String value, String description = null, String title = null){
        Configuration updateConfig = getConfigByKey( key )

        if( !updateConfig ){
            throw new DummiesException( DummiesMessageCode.CONFIG_DOES_NOT_EXISTS )
        }

        updateConfig.safeSetter([
            value: value,
            description: description,
            title: title
        ])

        updateConfig.save( flush: true, failOnError: true )

        updateConfig
    }

    Configuration getConfigByKey( String key ){
        Configuration.findByKey( key )
    }

    String getConfigurationValueByKey(String key ){
        getConfigByKey( key )?.value
    }

    String getDummiesHomeFolder(){
        getConfigurationValueByKey( CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER )
    }

    String getDummiesSaveResponseFolder(){
        getConfigurationValueByKey( CONFIG_KEY_SAVE_RESPONSE_FOLDER )
    }

    String getGlobalRedirectUrl(){
        getConfigurationValueByKey( CONFIG_KEY_GLOBAL_REDIRECT_URL )
    }

    Boolean getEnableGlobalRedirectUrl(){
        getConfigurationValueByKey( CONFIG_KEY_ENABLE_GLOBAL_REDIRECT_URL )
    }

    Boolean getSaveResponse(){
        Boolean.valueOf( getConfigurationValueByKey( CONFIG_KEY_SAVE_RESPONSE ) )
    }

    Boolean getOverrideSaveResponses(){
        Boolean.valueOf( getConfigurationValueByKey( CONFIG_KEY_OVERRIDE_SAVE_RESPONSE ) )
    }

    String getOverrideSaveResponsesExpression(){
        getConfigurationValueByKey( CONFIG_KEY_OVERRIDE_SAVE_RESPONSE_EXPRESSION )
    }

    String getAutoGenerateImportDummyFromResponses(){
        Boolean.valueOf( getConfigurationValueByKey( CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_FROM_RESPONSES ) )
    }


}
