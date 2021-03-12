/**
 * Helper class to manage custom validations
 */
var validator = {

    /**
     * Determines if a object is not null and undefined.
     *
     * @param obj
     * @returns {boolean}
     */
    isObject : function(obj){
        if(!obj || (Array.isArray(obj) && obj.length == 0)) {
            return false;
        }
        return true;
    },

    isArray : function(obj){
        Array.isArray(obj)
    },

    /**
     * Returns a valid object or a default value instead.
     */
    getValueOrDefault : function(obj, defaultVal){
        return validator.isObject(obj) ? obj : defaultVal;
    }
};