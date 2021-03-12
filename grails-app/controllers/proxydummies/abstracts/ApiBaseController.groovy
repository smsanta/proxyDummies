package proxydummies.abstracts

import grails.converters.JSON
import io.micronaut.http.HttpStatus
import proxydummies.exceptions.ApiException
import proxydummies.exceptions.DummiesException
import proxydummies.utilities.DummiesMessageCode

abstract class ApiBaseController extends AbstractController{

    static def ERROR_TYPE_NONE = 0
    static def ERROR_TYPE_NORMAL = 1
    static def ERROR_TYPE_WITH_DATA = 2

    /**
     * Perform a respond.
     *
     * @param status
     * @param result
     * @param asError
     *
     * @return
     */
    def protected doRespond(def status, def result, def asError = ERROR_TYPE_NONE, def attachCodeToResponseStatus = false){
        def resp = [status: status]

        switch (asError){
            case ERROR_TYPE_NORMAL:
                resp << [ message: result ]
                break;
            case ERROR_TYPE_WITH_DATA:
                resp << result
                break;
            case ERROR_TYPE_NONE:
                resp << [result: result]
                break;
        }

        if(attachCodeToResponseStatus){
            response.status = status
        }

        render(resp as JSON)
    }

    def protected respondOK(result) {
        doRespond(HttpStatus.OK.code, result);
    }

    //Error Responses
    def protected respondError(ApiException e){
        doRespond(e.code, [message : e.message, result: e.result], ERROR_TYPE_WITH_DATA)
    }

    def protected respondError(DummiesException e){
        doRespond(e.code, e.message, ERROR_TYPE_NORMAL)
    }

    def protected respondError(String message, int code = HttpStatus.INTERNAL_SERVER_ERROR.code) {
        doRespond(code, message, ERROR_TYPE_NORMAL)
    }

    def protected respondErrorAttachCode(String message, boolean attachCodeToResponseStatus = true, int code = HttpStatus.INTERNAL_SERVER_ERROR.code) {
        doRespond(code, message, ERROR_TYPE_NORMAL, attachCodeToResponseStatus)
    }

    /**
     * Executes an action handled for usual admin actions.
     * Knows how to handle AdminExceptions and general exceptions.
     *
     * @param handledAction
     * @param genericCode
     * @return
     */
    @Override
    protected def handle(Closure handledAction, def genericCode = DummiesMessageCode.GENERIC_ERROR){
        try {
            handledAction()
        } catch (ApiException e) {
            error("API Error(In action $actionName): -> ${e.message} \n ${DummiesException.getStringTrace(e)}")
            respondError(e)
        } catch (DummiesException e) {
            error("API Error(In action $actionName): -> ${e.message} \n ${DummiesException.getStringTrace(e)}")
            respondError(e)
        } catch (Exception e) {
            error("Error(In action $actionName): -> ${e.message} \n ${DummiesException.getStringTrace(e)}")
            respondError( DummiesException.getMessageByCode(genericCode), HttpStatus.INTERNAL_SERVER_ERROR.code)
        }
    }

    /**
     * Builds an url to redirect to the error view with his required params.
     *
     * @param message
     * @param title
     * @param redirectTo
     * @param btnLabel
     *
     * @return
     */
    protected String generateRedirectErrorUri(message, title, redirectTo, btnLabel){
        "/error?message=$message&title=$title&redirect=$redirectTo&btnlabel=$btnLabel"
    }
}
