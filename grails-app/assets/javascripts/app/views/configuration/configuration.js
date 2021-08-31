_configuration = {

    _btnSave: ".btn-update-config",
    _loaderId: "#loader-configuration",
    _saveResponseKey: "saveResponses",
    _headerSaveResponsesSelector: "#head-switch-save-responses",

    initialize: function () {
        _configuration.setSaveEvent();
        _configuration.setHeaderSaveResponseEvent();
    },

    setSaveEvent : function () {
        let btnsSave = $( _configuration._btnSave );

        $.each(btnsSave, function (index, btn) {
            $(btn).unbind().bind("click", function (e) {
                e.preventDefault();

                let configKey = $(this).attr("data-key");
                let configValue = $(this).parents(".card").find("textarea").val();

                app.startAjax( _configuration._btnSave, _configuration._loaderId, function () {
                    app.apiClient.updateConfiguration(configKey, configValue,
                        function (result) {
                            app.endAjax(_configuration._btnSave, _configuration._loaderId, function () {

                                if( configKey === _configuration._saveResponseKey ){
                                    let headerCheck = $( _configuration._headerSaveResponsesSelector );
                                    headerCheck.unbind("change").prop("checked", JSON.parse(configValue));
                                    _configuration.setHeaderSaveResponseEvent();
                                }
                                app.modals.showSuccess("Update Configuration", "La configuracion se ha guardado exitosamente.", function () {
                                    app.modals.closeDialog();
                                });

                            })
                        },
                        function (errorData) {
                            app.endAjax(_configuration._btnSave, _configuration._loaderId, function () {
                                app.modals.showError("Error al guardar la Rule", errorData.message );
                            })
                        }
                    )
                });
            })
        });
    },

    setHeaderSaveResponseEvent: function () {

        $( _configuration._headerSaveResponsesSelector ).change(function() {

            let configKey = _configuration._saveResponseKey;
            let configValue = this.checked;

            app.startAjax(_configuration._headerSaveResponsesSelector, _configuration._loaderId, function () {
                app.apiClient.updateConfiguration(configKey, configValue,
                    function (result) {
                        $("#ta-config-"+ configKey).val( configValue );
                        app.endAjax(_configuration._btnSave, _configuration._loaderId, function () {
                            app.modals.showSuccess("Update Configuration", "La configuracion se ha guardado exitosamente.", function () {
                                app.modals.closeDialog();
                            });

                        })
                    },
                    function (errorData) {
                        app.endAjax(_configuration._btnSave, _configuration._loaderId, function () {
                            app.modals.showError("Error al guardar la Rule", errorData.message );
                        })
                    }
                )
            });
        });
    }

};