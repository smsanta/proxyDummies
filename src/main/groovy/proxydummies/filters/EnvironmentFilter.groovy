package proxydummies.filters

import proxydummies.Environment
import proxydummies.abstracts.AbstractFilter

class EnvironmentFilter extends AbstractFilter {

    Long id
    String name
    String url
    String uriPrefix

    @Override
    def getTargetClass() {
        Environment
    }

    @Override
    Boolean isPaginationEnabled() {
        false
    }
}
