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
    final static String CONFIG_KEY_DEFAULT_AMBIENT = "defaultAmbientId"
    final static String CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_FROM_RESPONSES = "autoGenerateImportDummyFromResponses"
    final static String CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_DEFAULT_SERVICE_TYPE = "autoGenerateImportDummyDefaultServiceType"
    final static String CONFIG_KEY_DEFAULT_ENVIRONMENT = "defaultEnvironment"


    static List<String> getAllConfigurationKeys(){
        [
            CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER,
            CONFIG_KEY_SAVE_RESPONSE_FOLDER,
            CONFIG_KEY_GLOBAL_REDIRECT_URL,
            CONFIG_KEY_ENABLE_GLOBAL_REDIRECT_URL,
            CONFIG_KEY_SAVE_RESPONSE,
            CONFIG_KEY_OVERRIDE_SAVE_RESPONSE,
            CONFIG_KEY_OVERRIDE_SAVE_RESPONSE_EXPRESSION,
            CONFIG_KEY_DEFAULT_AMBIENT,
            CONFIG_KEY_OVERRIDE_SAVE_RESPONSE_EXPRESSION,
            CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_FROM_RESPONSES,
            CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_DEFAULT_SERVICE_TYPE
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
        getConfigurationValueByKey( CONFIG_KEY_ENABLE_GLOBAL_REDIRECT_URL ).toBoolean()
    }

    Boolean getSaveResponse(){
        getConfigurationValueByKey( CONFIG_KEY_SAVE_RESPONSE ).toBoolean()
    }

    Boolean getOverrideSaveResponses(){
        getConfigurationValueByKey( CONFIG_KEY_OVERRIDE_SAVE_RESPONSE ).toBoolean()
    }

    String getOverrideSaveResponsesExpression(){
        getConfigurationValueByKey( CONFIG_KEY_OVERRIDE_SAVE_RESPONSE_EXPRESSION )
    }

    Boolean getAutoGenerateImportDummyFromResponses(){
        getConfigurationValueByKey( CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_FROM_RESPONSES ).toBoolean()
    }

    String getAutoGenerateImportDummyDefaultServiceType(){
        getConfigurationValueByKey( CONFIG_KEY_AUTO_GENERATE_IMPORT_DUMMY_DEFAULT_SERVICE_TYPE )
    }

    Long getDefaultEnvironmentId(){
        getConfigurationValueByKey( CONFIG_KEY_DEFAULT_ENVIRONMENT ).toLong()
    }


}
