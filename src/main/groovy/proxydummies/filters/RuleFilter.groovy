package proxydummies.filters

import groovy.transform.ToString
import proxydummies.Rule
import proxydummies.abstracts.AbstractFilter

@ToString(includeNames = true)
class RuleFilter extends AbstractFilter{

    Long id
    String uri
    String method
    Boolean active
    String environmentName
    String environmentPrefix

    @Override
    def getTargetClass() {
        Rule
    }

    @Override
    Boolean isPaginationEnabled() {
        false
    }
}
