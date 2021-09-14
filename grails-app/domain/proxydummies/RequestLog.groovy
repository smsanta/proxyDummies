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
    String responseBody

    String rule

    static constraints = {
        rule nullable: true, blank: true
        urlDestination nullable: true, blank: true
        requestBody nullable: true, blank: true
        responseBody nullable: true, blank: true
    }

    static mapping = {
        version false
        responseBody sqlType: "LONG"
        requestBody length: 4000
        responseHeaders length: 4000
        requestHeaders length: 4000
        rule length: 4000
    }

    @Override
    def toMapObject() {
        [
            id: id,
            eventDate: eventDate?.format("HH:MM:ss.SSS"),
            uri: uri,
            forwarded: forwarded,
            urlDestination: urlDestination,
            requestHeaders: requestHeaders,
            requestType: requestType,
            responseStatus: responseStatus,
            responseHeaders: responseHeaders,
            rule: rule,
            requestBody: requestBody,
            responseBody: responseBody
        ]
    }
}
