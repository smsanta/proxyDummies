package proxydummies

import proxydummies.abstracts.AbstractObject

class RequestLog implements AbstractObject {

    Date eventDate = Date.newInstance()
    String uri
    Boolean forwarded
    String urlDestination

    String requestHeaders
    String requestBody
    String requestType

    Integer responseStatus
    String responseHeaders
    String zResponseBody

    static belongsTo = [rule: Rule]

    static constraints = {
        rule nullable: true
        urlDestination nullable: true, blank: true
        requestBody nullable: true, blank: true
        zResponseBody nullable: true, blank: true
    }

    static mapping = {
        version false
        requestBody length: 4000
        responseHeaders length: 4000
        requestHeaders length: 4000
        zResponseBody sqlType: "LONG"
    }

    @Override
    def toMapObject() {
        [
            id: id,
            eventDate: eventDate?.format("HH:mm:ss.SSS"),
            uri: uri,
            forwarded: forwarded,
            urlDestination: urlDestination,
            requestHeaders: requestHeaders,
            requestType: requestType,
            responseStatus: responseStatus,
            responseHeaders: responseHeaders,
            rule: rule,
            requestBody: requestBody,
            responseBody: zResponseBody
        ]
    }

    String getResponseBody(){
        zResponseBody
    }
}
