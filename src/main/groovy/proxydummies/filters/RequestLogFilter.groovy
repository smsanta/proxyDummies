package proxydummies.filters

import groovy.transform.ToString
import proxydummies.RequestLog
import proxydummies.Rule
import proxydummies.abstracts.AbstractFilter

@ToString(includeNames = true)
class RequestLogFilter extends AbstractFilter{

    Integer maxResults = 1000

    @Override
    def getTargetClass() {
        RequestLog
    }

    @Override
    Boolean isPaginationEnabled() {
        false
    }
}
