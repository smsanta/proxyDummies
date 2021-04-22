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

    /**
     * Does a set if the value is not null.
     *
     * @param toBe
     * @return
     */
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
