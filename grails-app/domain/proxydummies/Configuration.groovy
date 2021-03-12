package proxydummies

import proxydummies.abstracts.AbstractObject

class Configuration implements AbstractObject {

    String key
    String value

    static constraints = {
        key unique: true
    }

    static mapping = {
        version false
    }

    @Override
    def toMapObject() {
        [key: key, value: value]
    }
}
