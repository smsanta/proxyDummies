package proxydummies.abstracts

import proxydummies.SystemConfigsService
import proxydummies.exceptions.DummiesException
import proxydummies.utilities.DummiesMessageCode
import proxydummies.utilities.Logger
import org.springframework.web.context.request.RequestContextHolder

/**
 * This class is intended to extend funcionalities for
 * controllers and services classes and others wich runs
 * into Spring Context
 */
abstract class AbstractGenericImpl {

    static final String DEFAULT_RETRY_TIMES_PROP = "dummies.application.retryTimes"
    static final String DEFAULT_RETRY_SLEEP_TIME_PROP = "dummies.application.retrySleepTime"

    def grailsApplication
    SystemConfigsService systemConfigsService


    /**
     * Returns the grails application config.
     *
     * @return
     */
    protected getApplicationConfig(){
        grailsApplication.config
    }

    /**
     * Return a specific property from grails config.
     *
     * @param prop
     * @return
     */
    protected getApplicationConfigProperty(String prop){
        getApplicationConfig().getProperty( prop)
    }

    protected getApplicationConfigProperty(String prop, parseTo){
        getApplicationConfig().getProperty( prop, parseTo)
    }

    /**
     * Does an action with a retry policy.
     *
     * @param c
     * @return
     */
    static def retry(Closure c, retryTimes = null){
        retryTimes = retryTimes ?: getSystemConfigProperty( DEFAULT_RETRY_TIMES_PROP )
        def retrySleepTime = getSystemConfigProperty( DEFAULT_RETRY_SLEEP_TIME_PROP )

        Throwable catchedThrowable = null
        for(int i = 0; i < retryTimes; i++){
            try {
                return c.call()
            } catch(Throwable t){
                if (catchedThrowable == null) {
                    catchedThrowable = t
                }
                Thread.sleep(retrySleepTime)
            }
        }

        throw catchedThrowable
    }

    /**
     * Executes an action handled for usual admin actions.
     * Knows how to handle AdminExceptions and general exceptions.
     *
     * @param handledAction
     * @param genericCode
     * @return
     */
    protected def handle(Closure handledAction, def genericCode = DummiesMessageCode.GENERIC_ERROR){
        try {
            return handledAction()
        } catch (DummiesException e) {
            error("Error: ${e.message}")
            throw e
        } catch (Exception e) {
            error("Error: ${e.getClass()} - ${e.message} - ${DummiesException.getStringTrace(e)}")
            throw new DummiesException(genericCode)
        }
    }

    /**
     * Returns the current session.
     *
     * @return
     */
    protected getSession(){
        RequestContextHolder.currentRequestAttributes().getSession()
    }

    void setSessionAttributte(String key, def value){
        getSession().setAttribute(key, value)
    }

    def getSessionAttributte(String key){
        getSession().getAttribute( key )
    }

    void clearSessionAttribute( String key ){
        getSession().setAttribute(key, null)
    }

    void info(text) {
        Logger.info(this, text)
    }

    void error(text) {
        Logger.error(this, text)
    }

    protected String getSytemProperty( name ){
        System.getProperty( name )
    }

    protected String getConfiguration(String configProperty) {
        systemConfigsService.getConfigurationValueByKey( configProperty )
    }
}
