package proxydummies.command.rule

import proxydummies.Rule
import proxydummies.command.abstracts.MainCommand

class CreateRuleCommand extends MainCommand {

    String uri
    Integer priority
    Rule.SourceType sourceType
    String data
    Boolean active
    String description
    Boolean requestConditionActive
    String requestCondition
    Boolean isJson

    static constraints = {
        uri nullable: false, blank: false
        priority nullable: false, blank: false, min: 1
        sourceType nullable: false
        data nullable: false, blank: false
        active nullable: false
        description nullable: true, blank: true
        requestConditionActive nullable: true
        requestCondition nullable: true, blank: true, validator: { String value, CreateRuleCommand obj, errors ->
            if(obj.requestConditionActive && !value){
                errors.rejectValue("requestCondition", "requestConditionEmpty","Request Condition no puede estar vacío si la condición esta activa.")
            }
        }
        isJson nullable: true
    }

    @Override
    def toMapObject() {
        [
            uri: uri,
            priority: priority,
            sourceType: sourceType,
            data: data,
            active: active,
            description: description,
            isJson: isJson
        ]
    }
}
