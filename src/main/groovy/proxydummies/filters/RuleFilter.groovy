package proxydummies.filters

import groovy.transform.ToString
import proxydummies.Rule
import proxydummies.abstracts.AbstractFilter

@ToString(includeNames = true)
class RuleFilter extends AbstractFilter{

    Long id
    String uri
    Boolean active
    Long ambientId

    @Override
    def getTargetClass() {
        Rule
    }

    @Override
    Boolean isPaginationEnabled() {
        false
    }
}
