package proxydummies.abstracts


import org.grails.web.json.JSONElement
import org.grails.web.json.JSONObject

abstract class BaseService extends AbstractGenericImpl{

    //Service main attributes
    def restClient

    /**
     * Validates if a JSON is valid.
     *
     * @param json
     * @return
     */
    protected boolean isValidJSONStructure(JSONObject json) {

        boolean valid = true
        try {
            if (!json || json.has("error")) {
                valid = false
            }
        } catch (Exception e) {
            valid = false
        }
        valid
    }

    /**
     * Reruns the error string from a json error.
     *
     * @param json
     * @return
     */
    protected getJSONError(JSONElement json) {
        def message = null
        if (json && json.status != 200) {
            message = json.message
        }
        message
    }

}