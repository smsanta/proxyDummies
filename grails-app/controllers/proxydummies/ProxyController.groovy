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

        String environmentName = params.environment
        Environment environment = Environment.findByUriPrefix( environmentName )

        String requestUri = getRequestUri( environment )

        def checkRules = proxyService.getActiveRules( requestUri, request.method )

        String requestBody = request.getInputStream().getText( INPUT_STREAM_CHARSET_UTF8 )

        String redirectUrl = environment?.url

        if( systemConfigsService.getEnableGlobalRedirectUrl() ){
            redirectUrl = systemConfigsService.getGlobalRedirectUrl()
            info("Goblal redirect URL is enabled! forwaring request to: $redirectUrl" )
        }

        if( checkRules.isEmpty() ){
            info( "No Rules Matched for Uri: $requestUri. Forwaring to original destination.")
            forwardRequest( redirectUrl, requestUri, requestBody )
        } else {
            info( "We have found rules for this uri(${checkRules.size()}) --> $checkRules"  )
            Rule rule = proxyService.evalRules( checkRules, requestBody, params, requestUri )

            if( !rule ){
                info("Any rule applied to current request... Forwaring to $redirectUrl.")
                forwardRequest( redirectUrl, requestUri, requestBody )
                return
            }

            String dummy = proxyService.loadDummy( rule )

            rule.getResponseExtraHeadersObject()?.each { headerKey, headerValue ->
                response.addHeader( headerKey, headerValue )
            }

            if( rule.responseStatus ){
                response.status = rule.responseStatus
            }

            render( dummy )
        }
    }

    private String getRequestUri( Environment environment ) {
        String requestUri = proxyService.purgeProxyDummiesPrefix( request.getRequestURI() )

        if( environment ){
            requestUri = requestUri.replaceAll( "/${environment.uriPrefix}", "" )
        }

        requestUri
    }

    private void forwardRequest(String redirectUrl, String forwardUri, String requestBody ){
        info( "Forwaring request -> ${request.getRequestURL()} " )

        HttpClient httpClient = HttpClient.create( redirectUrl.toURL() )

        HttpRequest forwardRequest = getMirroredRequest( forwardUri, requestBody )
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
        mirroredRequest
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
