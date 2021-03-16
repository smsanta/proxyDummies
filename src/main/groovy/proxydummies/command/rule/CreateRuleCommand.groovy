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

    static constraints = {
        uri nullable: false, blank: false
        priority nullable: false, blank: false, min: 1
        sourceType nullable: false
        data nullable: false, blank: false
        active nullable: false
        description nullable: true, blank: true
        requestCondition nullable: true, blank: true, validator: { String value, CreateRuleCommand obj ->
            if(obj.requestConditionActive && !value){
                obj.errors.putAt("requestCondition", "requestCondition no puede estar vacío si la condición esta activa.")
            }
        }
    }

    @Override
    def toMapObject() {
        [
            uri: uri,
            priority: priority,
            sourceType: sourceType,
            data: data,
            active: active,
            description: description
        ]
    }
}
