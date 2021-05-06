package proxydummies

import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import proxydummies.abstracts.AbstractController
import proxydummies.exceptions.DummiesException
import proxydummies.utilities.DummiesMessageCode

class ProxyController extends AbstractController {

    ProxyService proxyService
    SystemConfigsService systemConfigsService

    def index() {

        String requestUri = proxyService.purgeProxyDummiesPrefix( request.getRequestURI() )

        String requestEnvironmentPrefix = requestUri.substring( 1, requestUri.indexOf("/", 1) )

        Environment requestEnvironment = Environment.findByUriPrefix( requestEnvironmentPrefix )

        requestUri = requestUri.replaceAll( requestEnvironmentPrefix, "" )

        def checkRules = proxyService.getActiveRules( requestUri, request.method, requestEnvironment )

        String requestBody = request.getInputStream().getText( INPUT_STREAM_CHARSET_UTF8 )

        if( checkRules.isEmpty() ){
            info( "No Rules Matched for Ur: $requestUri. Forwaring to original destination.")
            if( systemConfigsService.getEnableGlobalRedirectUrl() ){
                String redirectUrl = systemConfigsService.getGlobalRedirectUrl()
                info("Goblal redirect URL is enabled! forwaring request to: $redirectUrl" )
                forwardRequest( redirectUrl, requestUri, requestBody )
            } else {
                info( "There are no Rules matching current uri and global redirect is not enabled. Request will FAIL!." )
                throw new DummiesException( DummiesMessageCode.PROXY_NOWHERE_TO_REDIRECT )
            }
        } else {
            info( "We have found rules for this uri(${checkRules.size()}) --> $checkRules"  )
            Rule rule = proxyService.evalRules( checkRules, requestBody, params, requestUri )

            String dummy = proxyService.loadDummy( rule )

            rule.getResponseExtraHeadersObject().each { headerKey, headerValue ->
                response.addHeader( headerKey, headerValue )
            }

            if( rule.responseStatus ){
                response.status = rule.responseStatus
            }

            render( dummy )

        }
    }

    private void forwardRequest(String redirectUrl, String forwardUri, String soapBody ){
        info( "Forwaring request -> ${request.getRequestURL()} " )

        HttpClient httpClient = HttpClient.create( redirectUrl.toURL() )

        HttpRequest forwardRequest = getMirroredRequest( forwardUri, soapBody )
        HttpResponse newResponse

        try{
            newResponse = httpClient.toBlocking().exchange(forwardRequest, String)
        } catch(HttpClientResponseException e){
            newResponse = e.getResponse()
        }

        proxyService.saveResponse( forwardUri, newResponse.body() )
        mirrorResponseHeaders( newResponse, response )

        response.status = newResponse.getStatus().getCode()

        render( newResponse.body() )
    }

    private HttpRequest getMirroredRequest(String uri, String requestBody) {
        String method = request.method
        HttpRequest mirroredRequest = getRequestByMethod( method, uri, requestBody )
        mirrorCurrentRequestHeaders( mirroredRequest )
    }

    private HttpRequest getRequestByMethod(String method, String uri, String data ){
        HttpRequest newRequest
        if( HttpMethod."$method" == HttpMethod.POST ){
            newRequest = HttpRequest.POST( uri, data )
        } else {
            newRequest = HttpRequest.GET( uri )
        }

        newRequest
    }

}
