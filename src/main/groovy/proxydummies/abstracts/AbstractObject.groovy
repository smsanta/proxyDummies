package proxydummies.abstracts

import grails.converters.JSON
import proxydummies.utilities.Logger

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
        def mapObject = toMapObject()
        finalEdit( mapObject )
        mapObject
    }

    /**
     * Does a Set value "this.paramMapKey = paramMapValue"
     * skips setting a value if it is null.
     *
     * @param toBe
     * @return
     */
    def safeSetter( Map toBe ){
        def _this = this
        toBe.each { key, value ->
            if(value != null){
                try{
                    _this."$key" = value
                }catch(e){
                    Logger.error(this, "[safeSetter] Error setting Key $key with Value: $value On $_this")
                }
            }
        }

        _this
    }
}
