package proxydummies.command.rule

import proxydummies.command.abstracts.MainCommand

class ImportRuleCommand extends MainCommand{

    String uri
    String data
    String description
    Boolean requestConditionActive
    String requestCondition

    static constraints = {
        uri nullable: false, blank: false
        data nullable: false, blank: false
        description nullable: true, blank: true
        requestConditionActive nullable: true
        requestCondition nullable: true, blank: true, validator: { String value, ImportRuleCommand obj ->
            if(obj.requestConditionActive && !value){
                obj.errors.putAt("requestCondition", "requestCondition no puede estar vacío si la condición esta activa.")
            }
        }
    }

    @Override
    def toMapObject() {
        [
            uri: uri,
            data: data,
            description: description,
            requestConditionActive: requestConditionActive,
            requestCondition: requestCondition
        ]
    }
}
