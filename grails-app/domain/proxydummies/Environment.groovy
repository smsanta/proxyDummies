package proxydummies

import proxydummies.abstracts.AbstractObject

class Environment implements AbstractObject {

    String name
    String url
    String uriPrefix

    @Override
    def toMapObject() {
        [ name: name, url: url ]
    }

    static constraints = {
        name blank: false, unique: true
        url blank: false
    }

    static mapping = {
        version false
    }

}
