/**
 * This class will manage the internal comunication API.
 *
 * //Correct handling.
 * var sucessCallback = function( response ){
 *       alert( "The object is " + response )
 * }
 *
 * apiClient.getXObject(args.., sucessCallback)
 *
 * //Incorrect Handling
 * var sucessCallback = function( response ){
 *      if(response.status == 200){
 *          alert( "The object is " + response.result )
 *      }
 *      else if (response.status == xxx) {
 *          alert( "Error X " + response.message)
 *      }...
 * }
 *
 * apiClient.getXObject(args.., sucessCallback)
 */
var apiClient = {

    _DEFAULT_AJAX_ERROR_MESSAGE: "Ha ocurrido un error interno al ejecutar la acción.",
    _UNABLE_TO_REACH_SERVER: "No se ha podido establecer la comunicación con el servidor. Intente nuevamente mas tarde. Si el error persiste comuniquesé con el Administrador.",

    messages : {
        rule: {
            create: {
                error : "Error al crear la Rule."
            },
            update: {
                error: "Error al actualizar la Rule"
            },
            delete: {
                error: "Error al eliminar la Rule"
            },
            search: {
                error: "Error al buscar Rules"
            }
        },
        configuration: {
            updateConfiguration: {
                error : "Error al actualizar la configuración."
            },
            getConfiguration : {
                error : "Error al obtener la configuración."
            }
        }
    },
    /**
     * Defines the apis
     */
    module: {
        rule: "/setup/api/rule",
        configuration: "/setup/api/configuration"
    },

    /**
     * Defines action endpoint for each module api.
     */
    action: {
        //-- Rules --
        rule: {
            create: '/create',
            update: '/update',
            search: '/search',
            enable: '/enable',
            disable: '/disable',
            delete: '/delete'
        },
        configuration: {
            getConfiguration: '/find',
            update: '/update'
        }
    },

    _defaultErrorCallback: function (xhr, error, data) {
        app.log("AJAX ERROR: ");
        app.log(xhr);
        app.log(error);
        app.log(data);


        //$(".modal").modal("hide");

        app.modals.showError("Error de comunicación", apiClient._DEFAULT_AJAX_ERROR_MESSAGE );
    },

    //API Methods

    //    ########### Rule ###########
    createRule: function (pUri,pPriority, pSourceType, pData, pActive, pDescription, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.create;

        var json = {
            uri: pUri,
            priority: pPriority,
            sourceType: pSourceType,
            data: pData,
            active: pActive,
            description: pDescription
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( errorCallback ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.create.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    updateRule: function (pId, pUri,pPriority, pSourceType, pData, pActive, pDescription, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.update;

        var json = {
            id: pId,
            uri: pUri,
            priority: pPriority,
            sourceType: pSourceType,
            data: pData,
            active: pActive,
            description: pDescription
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( errorCallback ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.update.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    deleteRule: function (id, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.delete;

        var json = {
            id: id
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.delete.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    enableRule: function (id, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.enable;

        var json = {
            id: id
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.update.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    disableRule: function (id, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.disable;

        var json = {
            id: id
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.update.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    searchRules: function (filter, successCallback, errorCallback) {
        var urlFinal = app.config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.search;

        var json = filter.getJson();

        comunicator.doGet(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.search.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },
    //---------------------- Rule ----------------------

    //    ########### Configuration ###########
    updateConfiguration: function (pKey, pValue, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.configuration +
            apiClient.action.configuration.update;

        var json = {
            key: pKey,
            value: pValue
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.configuration.updateConfiguration.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    getConfiguration: function (pKey, successCallback, errorCallback) {
        var urlFinal = config.baseUrl +
            apiClient.module.configuration +
            apiClient.action.configuration.getConfiguration;

        var json = {
            key: pKey
        };

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.configuration.getConfiguration.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    }
};
