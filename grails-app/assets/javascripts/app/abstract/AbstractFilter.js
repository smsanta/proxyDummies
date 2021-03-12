class AbstractFilter extends DuumyObject{

    constructor( loadJson = {} ){
        super();
        this.loadJson( loadJson );
    }

    /**
     * Returns a JSON.
     *
     * @param asString - If true returns the stringify json.
     * @returns {{name: *}}
     */
    getJson(asString){
        let props = Object.getOwnPropertyNames( this );
        let json = {};

        props.forEach( prop => json[prop] = this[prop] );

        return asString ? JSON.stringify(json) : json;
    }

    /**
     * Returns the object with a query string format.
     *
     * @returns {string}
     */
    asQueryString(){
        return  $.param( this.getJson() );
    }

    /**
     * populates an Object from a json with matching attributes on it.
     * @param json
     * @returns The current filter with initialized filters
     */
     loadJson(json) {
         return $.extend(this, json);
     }
}