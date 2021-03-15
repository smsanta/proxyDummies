package proxydummies.abstracts

import grails.converters.JSON

trait AbstractObject {

    def toJsonObject(){
        toMapObject() as JSON
    }

    abstract def toMapObject()

    def toMapObject(List<String> includingKeys ){
        toMapObject().collectEntries { key, value ->
            key in includingKeys ? [key, value] : [:]
        }
    }
}
