_saveEnvironment = {

    _btnsSave: ".update-environment-btn",
    _btnsDelete: ".delete-environment-btn",
    _btnAddEnvironment : "#btn-add-environment",

    _loaderId: "#loader-save-environment",

    initialize: function () {
        _saveEnvironment.setGeneralEvents();
    },

    setGeneralEvents: function(){
        _saveEnvironment.setSaveEvents();
        _saveEnvironment.setDeleteEvents();
        _saveEnvironment.setAddNewEnvironmentEvent();
    },

    setSaveEvents: function(){
        $( _saveEnvironment._btnsSave ).unbind( "click" ).bind( "click", _saveEnvironment._onSaveEvent );
    },

    setDeleteEvents: function(){
        $( _saveEnvironment._btnsDelete ).unbind( "click" ).bind( "click", _saveEnvironment._onDeleteEvent );
    },

    setAddNewEnvironmentEvent: function(){
        $( _saveEnvironment._btnAddEnvironment ).unbind( "click" ).bind( "click", _saveEnvironment._onAddNewEnvironmentEvent );
    },

    _onDeleteEvent: $.noop,
    _onAddNewEnvironmentEvent: $.noop,

    _onSaveEvent : function (e) {
        e.preventDefault();
        let btnSave = $( this );
        let btnSaveId = "#" + btnSave.attr(id);

        let environmentData = _saveEnvironment.collectEnvironmentData();

        app.startAjax( btnSaveId, _saveEnvironment._loaderId, function () {
            app.apiClient.saveEnvironment( environmentData, function (result) {
                app.endAjax( btnSaveId, _saveEnvironment._loaderId, function () {
                    app.modals.showSuccess("Save Environment", "El environment se ha guardado exitosamente.", function () {
                        app.modals.closeDialog();
                    });
                })
            },
            function (errorData) {
                app.endAjax(btnSaveId, _saveEnvironment._loaderId, function () {
                    app.modals.showError("Error al guardar el Environment", errorData.message );
                })
            });
        });
    },

    collectEnvironmentData: function ( environmentId ) {
        let id = environmentId;
        let name = $("#abm_input_environment_name_" + id).val();
        let url = $("#abm_input_environment_url_prefix_" + id).val();
        let urlPrefix = $("#abm_input_environment_redirect_url_" + id).val();

        return new Environment({
            id: id,
            name: name,
            url: url,
            urlPrefix: urlPrefix
        })
    }
};