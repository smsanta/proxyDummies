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

    def toMapObject(Closure finalEdit){
        def mapObject = toMapObject();
        finalEdit( mapObject )
        return mapObject
    }

    def safeSetter( Map toBe ){
        def _this = this
        toBe.each { key, value ->
            if(value != null){
                _this."$key" = value
            }
        }

        _this
    }
}
