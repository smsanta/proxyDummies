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
    String responseExtraHeaders
    Integer responseStatus
    ServiceType serviceType
    String forwardUrl

    static belongsTo = [ environment: Environment ]

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
            environment: environment.toMapObject()
         ]
    }

    Map<String, String> getResponseExtraHeadersObject(){
        Eval.me( responseExtraHeaders )
    }

    String getDestination(){
        forwardUrl ?: environment.url
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
        FILE,
        DATABASE
    }

    enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        ANY
    }

    enum ServiceType {
        REST,
        SOAP,
        PROXY
    }

    static constraints = {
        description nullable: true
        requestCondition nullable: true
    }

    static mapping = {
        version false
        data type: "text"
    }
}
