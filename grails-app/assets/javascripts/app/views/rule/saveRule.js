_saveRule = {

    _btnSave : "#btn-save-rule",
    _loaderId: "#loader-save-rule",

    initialize: function () {
        _saveRule.setSaveEvent();
        $("#v-pills-save-rule-tab").bind( "click", _saveRule.clearForm )
    },

    setSaveEvent : function () {
        let btnSave = $( _saveRule._btnSave );

        btnSave.unbind().bind("click", function (e) {
            e.preventDefault();

            let ruleData = _saveRule.collectRuleData();

            let successCallback = function (result) {
                app.endAjax(_saveRule._btnSave, _saveRule._loaderId, function () {
                    app.modals.showSuccess("Save Rule", "La rule se ha guardado exitosamente.", function () {
                        app.modals.closeDialog();
                        _dashboard._onFilterStart();
                        app.navToDashboard();
                        _saveRule.clearForm();
                    });

                })
            };

            let errorCallback = function (errorData) {
                app.endAjax(_saveRule._btnSave, _saveRule._loaderId, function () {
                    app.modals.showError("Error al guardar la Rule", errorData.message );
                })
            };

            app.startAjax( _saveRule._btnSave, _saveRule._loaderId, function () {
                if( ruleData.id > 0 ){
                    app.apiClient.updateRule(
                        ruleData.id,
                        ruleData.uri,
                        ruleData.priority,
                        ruleData.sourceType,
                        ruleData.data,
                        ruleData.active,
                        ruleData.description,
                        successCallback,
                        errorCallback
                    )
                } else {
                    app.apiClient.createRule(
                        ruleData.uri,
                        ruleData.priority,
                        ruleData.sourceType,
                        ruleData.data,
                        ruleData.active,
                        ruleData.description,
                        successCallback,
                        errorCallback
                    )
                }

            });
        })
    },

    collectRuleData: function () {
        let id = $("#abm_input_id").val();
        let uri = $("#abm_input_uri").val();
        let priority = $("#abm_input_priority").val();
        let type = $("#abm_input_type").select().val();
        let data = $("#abm_input_data").val();
        let state = $("#abm_input_state").prop("checked");
        let description = $("#abm_input_description").val();

        return {
            id: id,
            uri: uri,
            priority: priority,
            sourceType: type,
            data: data,
            active: state,
            description: description
        }
    },

    clearForm: function () {
        $("#abm_input_uri").val("");
        $("#abm_input_priority").val("");
        $("#abm_input_state").prop("checked", false);
        $("#abm_input_type").select().val("FILE");
        $("#abm_input_data").val("");
        $("#abm_input_description").val("");
    },

    loadForm: function ( data ) {
        $("#abm_input_id").val( data.id );
        $("#abm_input_uri").val( data.uri );
        $("#abm_input_priority").val( data.priority );
        $("#abm_input_state").prop("checked", JSON.parse( data.active ));
        $("#abm_input_type").select().val( data.sourceType );
        $("#abm_input_data").val( data.data );
        $("#abm_input_description").val( data.description );

    }

};