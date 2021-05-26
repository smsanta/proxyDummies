package proxydummies

import proxydummies.abstracts.AbstractObject

class Environment implements AbstractObject {

    String name
    String url
    String uriPrefix

    @Override
    def toMapObject() {
        [ id: id, name: name, url: url, uriPrefix: uriPrefix ]
    }

    static constraints = {
        name blank: false, unique: true
        url blank: false
        uriPrefix blank: true
    }

    static mapping = {
        version false
    }

    @Override
    String toString() {
        toMapObject()
    }
}
