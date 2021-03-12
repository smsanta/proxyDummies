var comunicator = {

    /**
     * holds the response types supported by the AJAX actions.
     */
    _responseTypes : {
        TEXT : "text",
        JSON : "json",
        JSONP : "jsonp"
    },

    _requestType : {
        JSONP : "jsonp"
    },

    /**
     * Default JSONP callback.
     *
     * @param response
     * @private
     */
    _defaultJSONPCallback : function(response){
        app.log("JSONP default callback");
        app.log(response);
    },

    /**
     * Do a post request.
     *
     * @param url
     * @param dataParams
     * @param successCallback
     * @param errorCallback
     */
    doPost : function(url, dataParams, successCallback, errorCallback, responseType, autoStringify){
        if(!validator.isObject(responseType)){
            responseType = comunicator._responseTypes.TEXT
        }

        if(!validator.isObject(autoStringify)){
            autoStringify = true
        }
        comunicator._doAjax(url, 'POST', dataParams, responseType, successCallback, errorCallback, autoStringify)
    },

    /**
     * Do a get request.
     *
     * @param url
     * @param dataParams
     * @param successCallback
     * @param errorCallback
     */
    doGet : function (url, dataParams, successCallback, errorCallback, responseType) {
        if(!validator.isObject(responseType)){
            responseType = comunicator._responseTypes.TEXT
        }

        comunicator._doAjax(url, 'GET', dataParams, responseType, successCallback, errorCallback)
    },

    doFind : function(url, findFilter, successCallback, errorCallback, responseType){
        doGet()
    },

    /**
     * Executes an ajax call.
     *
     * @param url
     * @param type - GET - POST
     * @param params
     * @param responseType - json, text, etc.
     * @param errorCallback
     * @param successCallback
     * @private
     */
    _doAjax : function(url, type, params, responseType, successCallback, errorCallback, autoStringify){
        var formData = {};
        var connector = "?";

        if( type == "GET" ){
            var queryString = $.param( params );

            if( url.indexOf( connector ) > 0 ){
                connector = "&"
            }

            url = url + connector + queryString;
        } else {
            if( autoStringify ){
                params = JSON.stringify( params );
            }

            formData = params
        }

        $.ajax({
            url: url,
            type: type,
            data: formData,
            dataType : responseType,
            contentType : "application/json", //MUST BE CONTENT TYPE
            error: errorCallback,
            success: successCallback,
            //jsonpCallback : authCodeCallback,
            jsonp: false,
            async: true
        });
    },

    withRetry : function (url, type, params, responseType, successCallback, errorCallback, maxRetries) {
        var maxRetries = validator.getValueOrDefault(maxRetries, 3);
        var currentRetry = 1;


        var errCb = function(){
            app.log("err cb")
            currentRetry++;

            app.log("Retry: " + currentRetry)
            if(currentRetry == maxRetries){
                errorCallback()
            }else{
                setTimeout(function () {
                    app.log("timed out callback");
                    comunicator._doAjax.apply(callParams)
                }, 2000)
            }
        };

        var callParams = [url, type, params, responseType, successCallback, errCb]

        app.log("Doing ajax: ")
        return comunicator._doAjax.apply(callParams)
    }
};