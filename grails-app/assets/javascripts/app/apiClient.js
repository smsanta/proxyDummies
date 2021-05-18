/**
 * This class will manage the internal comunication API.
 *
 * //Correct handling.
 * let sucessCallback = function( response ){
 *       alert( "The object is " + response )
 * }
 *
 * apiClient.getXObject(args.., sucessCallback)
 *
 * //Incorrect Handling
 * let sucessCallback = function( response ){
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
                error: "Error al actualizar la Rule."
            },
            delete: {
                error: "Error al eliminar la Rule."
            },
            search: {
                error: "Error al buscar Rules."
            },
            getRuleDatabaseBody: {
                error: "Error al buscar el body de la Rule."
            },
            import: {
                error: "Error al importar Rules."
            },
            export: {
                error: "Error al exportar Rules."
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
            delete: '/delete',
            getRuleDatabaseBody: '/getRuleDatabaseBody',
            import: '/import',
            export: '/export'
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


        app.endAjax("", "span.spinner-border");

        app.modals.showError("Error de comunicación", apiClient._DEFAULT_AJAX_ERROR_MESSAGE );
    },

    //API Methods

    //    ########### Rule ###########
    createRule: function (rule, successCallback, errorCallback) {
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.create;

        let json = rule.getJson();

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

    updateRule: function (rule, successCallback, errorCallback) {
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.update;

        let json = rule.getJson();

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
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.delete;

        let json = {
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
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.enable;

        let json = {
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
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.disable;

        let json = {
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

    exportRule: function (id, successCallback, errorCallback) {
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.export;

        let json = {
            id: id
        };

        comunicator.doGet(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.export.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    importRule: function (pJson, successCallback, errorCallback) {
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.import;

        let json = pJson;

        comunicator.doPost(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.import.error, data.message );
                    }
                }
            },
            apiClient._defaultErrorCallback,
            comunicator._responseTypes.JSON
        )
    },

    searchRules: function (filter, successCallback, errorCallback) {
        let urlFinal = app.config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.search;

        let json = filter.getJson();

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

    getRuleDatabaseBody: function (id, successCallback, errorCallback) {
        let urlFinal = config.baseUrl +
            apiClient.module.rule +
            apiClient.action.rule.getRuleDatabaseBody;

        let json = {
            id: id
        };

        comunicator.doGet(urlFinal, json,
            function (data) {
                if (data.status == 200) {
                    successCallback(data.result);
                } else {
                    if( validator.isObject( errorCallback ) ){
                        errorCallback( data );
                    } else {
                        app.modals.showError(apiClient.messages.rule.getRuleDatabaseBody.error, data.message );
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
        let urlFinal = config.baseUrl +
            apiClient.module.configuration +
            apiClient.action.configuration.update;

        let json = {
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
        let urlFinal = config.baseUrl +
            apiClient.module.configuration +
            apiClient.action.configuration.getConfiguration;

        let json = {
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
