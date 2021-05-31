_saveEnvironment = {

    _btnsSave: "#v-pills-save-environment .update-environment-btn",
    _btnsDelete: "#v-pills-save-environment i[action=delete]",
    _btnAddEnvironment : "#btn-add-environment",

    _loaderId: "#loader-save-environment",

    _newRowSeed: 1000,

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

    _onSaveEvent : function (e) {
        e.preventDefault();
        let btnSave = $( this );
        let btnSaveId = "#" + btnSave.attr( "id" );
        let environmentId = btnSave.attr( "data-id" );

        let environmentData = _saveEnvironment.collectEnvironmentData( environmentId );

        let isNewEnvironment = btnSave.attr( "data-is-new" ) === "true";
        if( isNewEnvironment ){
            environmentData.id = undefined;
        }

        app.startAjax( btnSaveId, _saveEnvironment._loaderId, function () {
            app.apiClient.saveEnvironment( environmentData, function (result) {
                app.endAjax( btnSaveId, _saveEnvironment._loaderId, function () {
                    app.modals.showSuccess("Save Environment", "El environment se ha guardado exitosamente.");
                });

                if( isNewEnvironment ){
                    _saveEnvironment.updateNewlyAddedEnvironment(environmentId, result.id);
                }
            },
            function (errorData) {
                app.endAjax(btnSaveId, _saveEnvironment._loaderId, function () {
                    app.modals.showError("Error al guardar el Environment", errorData.message );
                })
            });
        });
    },

    _onDeleteEvent: function(e){
        e.preventDefault();
        let btnDelete = $( this );
        let btnRowContainer = btnDelete.parents(".row");
        let btnDeleteId = "#" + btnDelete.attr( "id" );
        let environmentId = btnDelete.attr( "data-id" );

        let isNewEnvironment = btnDelete.attr( "data-is-new" ) === "true";

        let environmentData = _saveEnvironment.collectEnvironmentData( environmentId );

        if( isNewEnvironment ){
            btnRowContainer.fadeOut("fast", function(){
                btnRowContainer.remove();
                _saveEnvironment.updateRowColours();
            });
        } else {
            app.modals.promtModal("Delete Environment", "Esta por eliminar el Environment: Nombre: " + environmentData.name, function(){
                app.modals.closeDialog();
                app.startAjax( btnDeleteId, _saveEnvironment._loaderId, function () {
                    app.apiClient.deleteEnvironment( environmentId, function (result) {
                        app.endAjax( btnDeleteId, _saveEnvironment._loaderId, function () {
                            app.modals.showSuccess("Delete Environment", "El environment se ha borrado exitosamente.");
                            btnRowContainer.fadeOut("fast", function(){
                                btnRowContainer.remove();
                                _saveEnvironment.updateRowColours();
                            });
                        })
                    },
                    function (errorData) {
                        app.endAjax(btnDeleteId, _saveEnvironment._loaderId, function () {
                            app.modals.showError("Error al eliminar el Environment", errorData.message );
                        })
                    });
                });
            });
        }
    },

    _onAddNewEnvironmentEvent: function (e) {
        e.preventDefault();

        let rowContainer = $(".environment-form");
        let lastRow = rowContainer.find(".row").last();

        let nextRowType = lastRow.hasClass( "even" ) ? "odd" : "even";
        let newRowId = _saveEnvironment.getEnvironmentIdSeed();

        let templateData = {
            "__ID__": newRowId,
            "__TYPE__": nextRowType,
        };

        let newRow = templater.getFilledTemplate( templateData, templater.TEMPLATE_ENVIRONMENT_ROW);

        rowContainer.append( newRow );

        rowContainer.find(".row").last().fadeIn("fast");

        _saveEnvironment.setGeneralEvents();
    },

    collectEnvironmentData: function ( environmentId ) {
        let id = environmentId;
        let name = $("#abm_input_environment_name_" + id).val();
        let url = $("#abm_input_environment_redirect_url_" + id).val();
        let uriPrefix = $("#abm_input_environment_url_prefix_" + id).val();

        return new Environment({
            id: id,
            name: name,
            url: url,
            uriPrefix: uriPrefix
        })
    },

    getEnvironmentIdSeed: function () {
        _saveEnvironment._newRowSeed = _saveEnvironment._newRowSeed + 1;
        return _saveEnvironment._newRowSeed;
    },

    updateNewlyAddedEnvironment: function(oldId, newId){
        let nameInput = $("#abm_input_environment_name_" + oldId);
        let urlInput = $("#abm_input_environment_redirect_url_" + oldId);
        let uriPrefixInput = $("#abm_input_environment_url_prefix_" + oldId);
        let btnDelete = $( "#delete-" + oldId );
        let btnSave = $( "#btn-save-environment-" + oldId );

        nameInput.attr( "id", "abm_input_environment_name_" + newId );
        urlInput.attr( "id", "abm_input_environment_redirect_url_" + newId );
        uriPrefixInput.attr( "id", "abm_input_environment_url_prefix_" + newId );

        btnDelete.attr( "data-id", newId );
        btnDelete.attr( "id", "delete-" + newId );

        btnSave.attr( "data-id", newId );
        btnSave.attr( "id", "btn-save-environment-" + newId );
        btnSave.parents( ".row" ).attr( "data-id", newId );
    },

    updateRowColours: function () {
        let rowList = $(".environment-form .row");

        $.each( rowList, function(index, item){
            let newType = (index % 2) == 0 ? "odd" : "even";
            $(item).removeClass("even")
                .removeClass("odd")
                .addClass(newType);
        });
    }
};