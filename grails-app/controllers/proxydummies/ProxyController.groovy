package proxydummies

import grails.converters.JSON
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.ReadTimeoutException

import java.time.Duration
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.DefaultHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.uri.UriBuilder
import proxydummies.abstracts.AbstractController

class ProxyController extends AbstractController {

    ProxyService proxyService
    SystemConfigsService systemConfigsService

    def index() {

        String environmentName = params.environment
        Environment environment = Environment.findByUriPrefix( environmentName )
        info( "Environment Key: ${ environmentName } - Obtained -> $environment" )

        String requestUri = getRequestUri( environment )

        def checkRules = proxyService.getActiveRules( requestUri, request.method )

        String requestBody = request.getInputStream().getText( INPUT_STREAM_CHARSET_UTF8 )

        String redirectUrl = environment?.url

        Boolean isGlobalRedirectEnabled = systemConfigsService.getEnableGlobalRedirectUrl()
        if( isGlobalRedirectEnabled ){
            redirectUrl = systemConfigsService.getGlobalRedirectUrl()
            info("Goblal redirect URL is enabled! forwaring request to: $redirectUrl" )
        }

        if( checkRules.isEmpty() ){
            info( "No Rules Matched for Uri: $requestUri. Forwaring to original destination.")
            if( !isGlobalRedirectEnabled && !environment){
                info( "There is nowhere to redirect current request. FAILING!!!" )
                render(status: 500, text: "Environment (${params.environment}) does not exist. The request has nowhere to go.")
                return
            }
            forwardRequest( redirectUrl, requestUri, requestBody )
        } else {
            info( "We have found rules for this uri(${checkRules.size()}) --> $checkRules"  )
            Rule rule = proxyService.evalRules( checkRules, requestBody, params, requestUri )

            if( !rule ){
                info("Any rule applied to current request... Forwaring to $redirectUrl.")
                if( isGlobalRedirectEnabled == false && environment == false ){
                    info( "There is nowhere to redirect current request. FAILING!!!" )
                    render(status: 500, text: "Environment (${params?.environment}) does not exist. The request has nowhere to go.")
                    return
                }
                forwardRequest( redirectUrl, requestUri, requestBody )
                return
            }

            String dummy = proxyService.loadDummy( rule )

            rule.getResponseExtraHeadersObject()?.each { String headerKey, String headerValue ->
                response.addHeader( headerKey, headerValue )
            }

            if( rule.responseStatus ){
                response.status = rule.responseStatus
            }

            registerRequestLog( requestUri, false, null, requestBody, request.method, response.status, getResponseHeadersMap(response), dummy, rule )

            render( dummy )
        }
    }

    private void registerRequestLog(
        String uri,
        Boolean forwarded,
        String redirectUrl,
        String requestBody,
        String requestType,
        Integer responseStatus,
        LinkedHashMap responseHeadersMap,
        String responseBody,
        Rule rule
    ){
        String requestHeaders = (getRequestHeadersMap() as JSON).toString()
        String responseHeaders = (responseHeadersMap as JSON).toString()
        String plainRule = rule ? (rule.toMapObject() as JSON).toString() : ""

        proxyService.registerRequestLog (
            Date.newInstance(),
            uri,
            forwarded,
            redirectUrl,
            requestHeaders,
            requestBody,
            requestType,
            responseStatus,
            responseHeaders,
            responseBody,
            plainRule
        )
    }

    private String getRequestUri( Environment environment ) {
        String requestUri = proxyService.purgeProxyDummiesPrefix( request.getRequestURI() )

        if( environment ){
            requestUri = requestUri.replaceAll( "/${environment.uriPrefix}", "" )
        }

        requestUri
    }

    private void forwardRequest(String redirectUrl, String forwardUri, String requestBody ){
        info( "Forwaring request -> ${request.getRequestURL()} to ${redirectUrl}${forwardUri}." )

        DefaultHttpClient httpClient = HttpClient.create( redirectUrl.toURL() )
        httpClient.configuration.exceptionOnErrorStatus = false
        httpClient.configuration.readTimeout = Duration.ofSeconds( 30 )
        HttpRequest forwardRequest = getMirroredRequest( forwardUri, requestBody )

        HttpResponse newResponse
        try {
            newResponse = httpClient.toBlocking().exchange(forwardRequest, String)
        }catch(ReadTimeoutException e){
            //TODO Buscar la forma de enviar el status code de timeout cuando ocurre la excepcion
            response.status = HttpStatus.REQUEST_TIMEOUT.getCode()
            newResponse = response
        }

        proxyService.saveResponse( forwardUri, newResponse.body(), forwardRequest.method.name() )
        mirrorResponseHeaders( newResponse, response )

        String responseBody = newResponse.body() ?: ""

        //TODO: Check why length m8 not be matching original response length.
        if( responseBody ){
            response.addHeader("content-length", newResponse.body().length().toString() )
        }

        response.status = newResponse.getStatus().getCode()

        registerRequestLog( forwardUri, true, redirectUrl, requestBody, request.method, response.status, getResponseHeadersMap(response), responseBody, null)

        render( responseBody )
    }

    private HttpRequest getMirroredRequest(String uri, String requestBody) {
        String method = request.method
        HttpRequest mirroredRequest = getRequestByMethod( method, uri, requestBody )
        mirrorCurrentRequestHeaders( mirroredRequest )
        mirroredRequest
    }

    private HttpRequest getRequestByMethod(String method, String uri, String data ){
        HttpRequest newRequest

        UriBuilder uriBuilder = UriBuilder.of( uri )

        params.each { key, value ->
            if( !(key in ["action", "controller", "environment"]) ){
                uriBuilder.queryParam( key, value )
            }
        }

        URI requestUri = uriBuilder.build()
        info( "Building request with uri: $requestUri" )
        if( HttpMethod."$method" == HttpMethod.POST ){
            newRequest = HttpRequest.POST( requestUri, data )
        } else {
            newRequest = HttpRequest.GET( requestUri )
        }

        newRequest
    }
}
