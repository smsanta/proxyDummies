package proxydummies

import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import proxydummies.abstracts.AbstractController

class ProxyController extends AbstractController{

    ProxyService proxyService
    SystemConfigsService systemConfigsService

    def index() {

        def requestUri = proxyService.purgeProxyDummiesPrefix( request.getRequestURI() )
        def checkRules = proxyService.getActiveRules( requestUri )

        String requestBody = request.getInputStream().getText("UTF8")
        String requestSoapBody = requestBody

        if( checkRules.isEmpty() ){
            info( "No Rules Matched for Ur: $requestUri. Forwaring to original destination.")
            forwardRequest( requestUri, requestSoapBody )
        } else {
            def xmlRequestNavigator = XmlNavigator.newInstance( requestSoapBody )

            info( "We have found rules for this uri(${checkRules.size()}) --> $checkRules"  )
            Rule rule = proxyService.evalRules( checkRules, xmlRequestNavigator )

            if ( !rule ){
                info("Ninguna de las rules testeadas pudieron ser verificadas. se forwarea la request por default.")
                forwardRequest( requestUri, requestSoapBody )
            }else{
                String dummy = proxyService.loadDummy( rule )

                response.addHeader('Content-Type', 'text/xml')

                render( dummy )
            }
        }
    }

    private void forwardRequest(forwardUri, String soapBody ){
        info( "Forwaring request -> ${request.getRequestURL()} " )

        String redirectUrl = systemConfigsService.getDummiesRedirectUrl()
        HttpClient httpClient = HttpClient.create( redirectUrl.toURL() )


        HttpRequest forwardRequest = getRequestByMethod( request.method, forwardUri, soapBody )
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


    private HttpRequest getRequestByMethod(String method, String uri, String data ){
        HttpRequest newRequest
        if( HttpMethod."$method" == HttpMethod.POST ){
            newRequest = HttpRequest.POST( uri, data )
        } else {
            newRequest = HttpRequest.GET( uri )
        }

        mirrorCurrentRequestHeaders( newRequest )

        newRequest
    }

}
