package proxydummies.abstracts

import grails.web.databinding.DataBinder

/**
 * Trait for extend Commands Usage
 */
trait Populable implements DataBinder{

    /**
     * A workaround for use the method populate in a constructor.
     *
     * def params = [name: "xxx"]
     *
     * Example: APopulableClass.newInstance( populate : params ]
     *
     * @param data
     * @param postBind
     */
    void setPopulate(data, Closure postBind = null){
        populate(data, postBind)
    }

    def populate(data, Closure postBind = null){
        bindData(this, data)

        if( postBind ){
            postBind (this, data)
        }

        this
    }

}