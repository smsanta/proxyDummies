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
        [
            id: id,
            uri: uri,
            active: active,
            priority: priority,
            description: description,
            sourceType: sourceType.toString(),
            data: data,
            requestConditionActive: requestConditionActive,
            responseOverrideActive: responseOverrideActive
        ]
    }

    enum SourceType {
        FILE,
        DATABASE,
        SERVICE
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
