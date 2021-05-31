package proxydummies.command.rule

import proxydummies.Rule
import proxydummies.command.abstracts.MainCommand

class ImportRuleCommand extends MainCommand{

    String uri
    String data
    String description
    Boolean requestConditionActive
    String requestCondition
    Rule.HttpMethod method
    Rule.ServiceType serviceType
    Integer responseStatus
    String responseExtraHeaders

    static constraints = {
        uri nullable: false, blank: false
        data nullable: false, blank: false
        method nullable: false, blank: false

        serviceType nullable: true, blank: true
        responseExtraHeaders nullable: true, blank: true
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
