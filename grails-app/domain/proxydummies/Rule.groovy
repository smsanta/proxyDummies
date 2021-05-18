package proxydummies

import proxydummies.abstracts.AbstractObject

class Rule implements AbstractObject{

    String uri
    String description = ""
    Integer priority
    Boolean active = false
    String data
    Boolean requestConditionActive = false
    String requestCondition = ""

    SourceType sourceType
    HttpMethod method
    String responseExtraHeaders = ""
    Integer responseStatus = 200
    ServiceType serviceType

    @Override
    def toMapObject() {
         [
            id: id,
            uri: uri,
            data: data,
            active: active,
            priority: priority,
            description: description,
            sourceType: sourceType.name(),
            requestConditionActive: requestConditionActive,
            requestCondition: requestCondition,
            method: method.name(),
            serviceType: serviceType.name(),
            responseStatus: responseStatus,
            responseExtraHeaders: responseExtraHeaders ?: ""
         ]
    }

    Map<String, String> getResponseExtraHeadersObject(){
        responseExtraHeaders ? Eval.me( responseExtraHeaders ) : [:]
    }

    @Override
    String toString() {
        toMapObject({
            if( sourceType == SourceType.DATABASE ){
                it.data = ""
            }
        }).toString()
    }

    enum SourceType {
        DATABASE,
        FILE
    }

    enum HttpMethod {
        POST,
        GET,
        PUT,
        DELETE,
        ANY
    }

    enum ServiceType {
        SOAP,
        REST,
        PROXY
    }

    static constraints = {
        description nullable: true
        requestCondition nullable: true
        responseExtraHeaders nullable: true, blank: true
    }

    static mapping = {
        version false
        data type: "text"
    }
}
