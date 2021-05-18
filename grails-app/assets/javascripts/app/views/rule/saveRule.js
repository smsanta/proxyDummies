_saveRule = {

    _btnSave : "#btn-save-rule",
    _loaderId: "#loader-save-rule",

    initialize: function () {
        _saveRule.setSaveEvent();
        _saveRule.setTextAreaEditEvents();
        _saveRule.setGeneralEvents();
    },

    setGeneralEvents: function(){
        $("#v-pills-save-rule-tab").bind( "click", _saveRule.clearForm )

        $("#abm_input_request_condition_active").bind("change", function () {
            let isChecked = $(this).prop("checked");
            $("#abm_input_request_condition").attr("disabled", !isChecked);
        });
    },

    setTextAreaEditEvents: function(){
        $("#abm_input_data").unbind("click").bind("click", _saveRule.popupEditData);
        $("#abm_input_request_condition").unbind("click").bind("click", _saveRule.popupEditRequestCondition);
    },

    setSaveEvent : function () {
        let btnSave = $( _saveRule._btnSave );

        btnSave.unbind().bind("click", function (e) {
            e.preventDefault();

            let ruleData = _saveRule.collectRuleData();

            let successCallback = function (result) {
                app.endAjax(_saveRule._btnSave, _saveRule._loaderId, function () {
                    app.modals.showSuccess("Save Rule", "La rule se ha guardado exitosamente.", function () {
                        _dashboard._rule_data_cache[ruleData.id] = undefined;
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


            ruleData.data = encodeURIComponent( ruleData.data );

            app.startAjax( _saveRule._btnSave, _saveRule._loaderId, function () {
                if( ruleData.id > 0 ){
                    app.apiClient.updateRule(ruleData, successCallback, errorCallback );
                } else {
                    app.apiClient.createRule(ruleData, successCallback, errorCallback );
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
        let requestConditionActive = $("#abm_input_request_condition_active").prop("checked");
        let requestCondition = $("#abm_input_request_condition").val();
        let method = $("#abm_input_method").select().val();
        let serviceType = $("#abm_input_service_type").select().val();
        let responseStatus = $("#abm_input_response_status").val();
        let responseHeaders = $("#abm_input_respone_headers").val();

        return new Rule({
            id: id,
            uri: uri,
            priority: priority,
            sourceType: type,
            data: data,
            active: state,
            description: description,
            requestCondition: requestCondition,
            requestConditionActive: requestConditionActive,
            method: method,
            serviceType: serviceType,
            responseStatus: responseStatus,
            responseHeaders: responseHeaders,
        })
    },

    clearForm: function () {
        _saveRule._fillFillForm( config.view.saveRule.formDefaults );
    },

    loadForm: function ( data ) {
        _saveRule._fillFillForm( data );
    },

    _fillFillForm: function( data ){
        $("#abm_input_id").val( data.id );
        $("#abm_input_uri").val( data.uri );
        $("#abm_input_priority").val( data.priority );
        $("#abm_input_state").prop("checked", JSON.parse( data.active ));
        $("#abm_input_type").select().val( data.sourceType );
        $("#abm_input_data").val( data.data );
        $("#abm_input_description").val( data.description );
        $("#abm_input_request_condition_active").prop( "checked", data.requestConditionActive );
        $("#abm_input_request_condition").attr( "disabled", !data.requestConditionActive );
        $("#abm_input_request_condition").val( data.requestCondition );
        $("#abm_input_method").select().val( data.method );
        $("#abm_input_service_type").select().val( data.serviceType );
        $("#abm_input_response_status").val( data.responseStatus );
        $("#abm_input_respone_headers").val( data.responseHeaders );
    },

    popupEditData: function () {
        let ruleData = _saveRule.collectRuleData();

        if( ruleData.sourceType == "DATABASE"){
            let ruleId = ruleData.id;

            let ruleSavedData = $("#abm_input_data").val();
            if( $("#abm_input_data").val() == "" ){
                ruleSavedData = (_dashboard._rule_data_cache[ruleId] !== undefined) ? _dashboard._rule_data_cache[ruleId] : ruleData.data;
            }

            let dataPopup = '<textarea id="ta-pop-'+  ruleId +'" class="w-100 h-100 border-0">' + Util.escapeXml( ruleSavedData ) + '</textarea>';
            app.modals.showPopup("Data from -> " + ruleData.uri,  dataPopup, function () {
                let newRuleData = $('#ta-pop-'+  ruleId).val();
                $("#abm_input_data").val( newRuleData );
                app.modals.closeDialog();
            });
        }
    },

    popupEditRequestCondition: function () {
        let ruleData = _saveRule.collectRuleData();

        if( ruleData.requestConditionActive == true){
            let ruleId = ruleData.id;

            let testButton = '<a href="https://groovyconsole.appspot.com/edit/5072938134929408" ' +
                'target="_blank" class="btn btn-warning" ' +
                'style="float: right; margin-right: 8px;" ' +
                'data-bs-toggle="tooltip" data-bs-placement="right" title="Si aparece en blanco, intenta recargar la pagina."' +
                '>Probar Expression</a>';

            let infoIcons =
                '<a href="https://docs.groovy-lang.org/latest/html/api/groovy/util/XmlParser.html" target="_blank" style="float: right; margin-right: 10px;">' +
                    '<i class="bi-info-circle-fill text-blue" data-bs-toggle="tooltip" data-bs-placement="left" title="" data-original-title="Click para ver sobre XmlParser"></i></a>' +
                '<a href="https://github.com/smsanta/proxyDummies/tree/master/grails-app/utils/proxydummies/XmlNavigator.groovy" target="_blank" style="float: right; margin-right: 10px;">' +
                    '<i class="bi-info-circle-fill text-blue" data-bs-toggle="tooltip" data-bs-placement="left" title="" data-original-title="Click para ver la clase wrapper."></i></a>';
            let dataPopup = '<textarea id="ta-pop-rc-'+  ruleId +'" class="w-100 h-100 border-0">' + ruleData.requestCondition + '</textarea>';

            app.modals.showPopup("Request Condition from -> " + ruleData.uri + testButton + infoIcons,  dataPopup, function () {
                let newRuleRequestCondition = $('#ta-pop-rc-'+  ruleId).val();
                $("#abm_input_request_condition").val( newRuleRequestCondition );
                app.modals.closeDialog();
            });

            setTimeout(function () {
                app.startTooltips();
            }, 1000)
        }
    }

};