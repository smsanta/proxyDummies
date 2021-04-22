package proxydummies.filters

import proxydummies.Ambient
import proxydummies.abstracts.AbstractFilter

class AmbientFilter extends AbstractFilter {

    Long id
    String name
    String url

    @Override
    def getTargetClass() {
        Ambient
    }

    @Override
    Boolean isPaginationEnabled() {
        false
    }
}
