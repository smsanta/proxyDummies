package proxydummies

import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import proxydummies.abstracts.AbstractController

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletResponse
import javax.xml.soap.MessageFactory
import javax.xml.soap.MimeHeaders
import javax.xml.soap.SOAPMessage

class ProxyController extends AbstractController{

    ProxyService proxyService
    SystemConfigsService systemConfigsService

    def index() {

        def requestUri = proxyService.purgeProxyDummiesPrefix( request.getRequestURI() )
        def checkRules = proxyService.getActiveRules( requestUri )

        String requestBody = request.getInputStream().getText("UTF8")
        //String requestSoapBody = getSoapBody()
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
        HttpResponse newResponse = httpClient.toBlocking().exchange(forwardRequest, String)

        saveResponse( newResponse.body(), forwardUri )

        mirrorResponseHeaders( newResponse, response )

        response.status = newResponse.getStatus().getCode()

        render( newResponse.body() )
    }

    private void saveResponse(String data, String uriName) {
        String dummyName = proxyService.generateDummyNameFromUri( uriName )

        proxyService.createDummy( data, dummyName )
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

    private String getSoapBody(){
        MessageFactory messageFactory = MessageFactory.newInstance();
        ServletInputStream servletInputStream = request.getInputStream();
        SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), (InputStream)servletInputStream)
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        soapMessage.writeTo(out)
        return new String(out.toByteArray())
    }

}
