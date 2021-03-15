package proxydummies

import proxydummies.abstracts.AbstractObject

class Rule implements AbstractObject{

    String uri
    String description
    Integer priority
    Boolean active = false
    String data
    Boolean requestConditionActive = false
    Boolean responseOverrideActive = false

    String requestCondition
    String responseOverride

    SourceType sourceType

    @Override
    def toMapObject() {
        def mapObject = [
            id: id,
            uri: uri,
            active: active,
            priority: priority,
            description: (description ? description : ""),
            sourceType: sourceType.toString(),
            data: "",
            requestConditionActive: requestConditionActive,
            responseOverrideActive: responseOverrideActive
        ]

        if( sourceType in [ SourceType.FILE ] ){
            mapObject.data = data
        }

        mapObject
    }

    enum SourceType {
        FILE,
        DATABASE
        //SERVICE
    }

    static constraints = {
        description nullable: true
        requestCondition nullable: true
        responseOverride nullable: true
    }

    static mapping = {
        version false
        data type: "text"
    }
}
