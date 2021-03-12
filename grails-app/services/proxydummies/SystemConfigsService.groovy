package proxydummies

import grails.gorm.transactions.Transactional
import proxydummies.abstracts.BaseService
import proxydummies.exceptions.DummiesException
import proxydummies.utilities.DummiesMessageCode

@Transactional
class SystemConfigsService extends BaseService{

    final static String CONFIG_KEY_REDIRECT_URL = "redirectUrl"
    final static String CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER = "proxyDummiesHome"
    final static String CONFIG_KEY_SAVE_RESPONSE = "saveResponses"
    final static String CONFIG_KEY_SAVE_RESPONSE_FOLDER = "saveResponsesFolder"

    static List<String> getAllConfigurationKeys(){
        [
                CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER,
                CONFIG_KEY_SAVE_RESPONSE_FOLDER,
                CONFIG_KEY_REDIRECT_URL,
                CONFIG_KEY_SAVE_RESPONSE
        ]
    }

    Configuration createNewConfig( key, value ){
        Configuration newConfig = getConfigByKey( key )

        if( !newConfig ){
            newConfig = Configuration.newInstance()
            newConfig.key = key
        }

        newConfig.value = value

        newConfig.save( flush: true )

        newConfig
    }

    Configuration updateConfig( key, value ){
        Configuration updateConfig = getConfigByKey( key )

        if( !updateConfig ){
            throw new DummiesException( DummiesMessageCode.CONFIG_DOES_NOT_EXISTS )
        }

        updateConfig.value = value

        updateConfig.save( flush: true )

        updateConfig
    }

    Configuration getConfigByKey( key ){
        Configuration.findByKey( key )
    }

    String getConfigValueByKey( key ){
        getConfigByKey( key )?.value
    }

    String getDummiesHomeFolder(){
        getConfigValueByKey( CONFIG_KEY_PROXY_DUMMIES_HOME_FOLDER )
    }

    String getDummiesSaveResponseFolder(){
        getConfigValueByKey( CONFIG_KEY_SAVE_RESPONSE_FOLDER )
    }

    String getDummiesRedirectUrl(){
        getConfigValueByKey( CONFIG_KEY_REDIRECT_URL )
    }

    Boolean getSaveResponse(){
        Boolean.valueOf( getConfigValueByKey( CONFIG_KEY_SAVE_RESPONSE ) )
    }

}
