package proxydummies

import proxydummies.abstracts.AbstractObject

class Ambient implements AbstractObject{

    String name
    String url

    static constraints = {
        url blank: false
    }

    @Override
    def toMapObject() {
        [ id: id, name: name, url: url ]
    }
}
