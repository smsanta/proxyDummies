package proxydummies.abstracts

import io.micronaut.http.HttpRequest
import proxydummies.exceptions.ApiException
import proxydummies.utilities.DummiesMessageCode
import org.springframework.http.HttpStatus

abstract class AbstractController extends AbstractGenericImpl{

    /**
     * Returns the request parameters
     *
     * @param source accepts multiple parameters for different method
     * -> true = Returns GET and POST parameters all merged.
     *          (Careful if are 2 same keys in get and post the post will have priority)
     * -> HttpStatus.GET -> Retuns the GET parameters only
     * -> HttpStatus.POST -> Retuns the POST parameters only
     * -> null -> returns a map with bot parameters but separated [GET: _, POST: _]
     *
     *
     * @return By default returns the GET and POST merged parameters.
     */
    protected getRequestParams(source = true){
        def reqParams = [ GET: params, POST: request.JSON ]

        if(source == true){
            reqParams.GET + reqParams.POST
        }else{
            reqParams["${source}"] ?: reqParams
        }
    }

    /**
     * Generates a command Object and populates it
     *
     * @param tg - Is the command object to be populated
     * @param source - Is which method is going to be used
     *        (HttpMethod.GET, HttpMethod.POST or null for BOTH)
     * @return
     */
    protected def getCommandInstance(def tg, def source= true, Closure postBind = null){
        def toParamsBind = getRequestParams(source);

        bindData(tg, toParamsBind)

        if( postBind ){
            postBind (tg, toParamsBind)
        }

        tg
    }

    protected def getCommandAndValidate(def tg, def source=true, Closure postBind = null){
        def command = getCommandInstance(tg, source, postBind)

       validateCommand(command)

        command
    }

    protected void validateCommand( command ){
        if(!command.validate()){
            throw new ApiException( DummiesMessageCode.GENERIC_COMMAND_VALIDATION_REJECTION, command.getErrorMap());
        }
    }

    protected mirrorResponseHeaders(copyHeader, pasteHeaders) {
        def headers = copyHeader.getHeaders()
        headers.each { header ->
            info( "Mirroring Response Headers: ${header.key} -> ${header.value}" )
            pasteHeaders.addHeader( header.key, header.value.first() )
        }
    }

    private void mirrorCurrentRequestHeaders(HttpRequest newRequest ){
        Enumeration<String> headerNames = request.getHeaderNames()
        while ( headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement()
            def header = request.getHeader(  nextElement )
            info( "Mirroring Request Headers: $nextElement -> $header")
            newRequest.headers.add( nextElement, header )
        }
    }

    def methodNotAllowed() {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value())
    }

    protected def isAjaxRequest(){
        params.request_type == "ajax"
    }

    protected URL getDummyUrl(){

    }


}
