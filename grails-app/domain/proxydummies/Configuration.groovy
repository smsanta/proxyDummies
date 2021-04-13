package proxydummies

import proxydummies.abstracts.AbstractObject

class Configuration implements AbstractObject {

    String key
    String value
    String description
    String title

    static constraints = {
        key unique: true
        description nullable: true
        title nullable: true
    }

    static mapping = {
        version false
    }

    @Override
    def toMapObject() {
        [key: key, value: value, description: description, title: title]
    }
}
