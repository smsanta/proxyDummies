var _dashboard = {

    _btnFilter: "#btn-search",
    _loaderId: "#loader-dashboard",

    initialize: function () {
        _dashboard.setFilterEvent();

    },

    setFilterEvent : function () {
        let btnFilter = $( _dashboard._btnFilter );

        btnFilter.unbind().bind("click", function (e) {
            e.preventDefault();
            _dashboard._onFilterStart();
        })
    },

    _onFilterStart : function(){
        let searchFilter = _dashboard.getSearchFilter();

        app.startAjax( _dashboard._btnFilter, _dashboard._loaderId, function () {
            app.apiClient.searchRules(searchFilter,
                function (result) {
                    app.endAjax( _dashboard._btnFilter, _dashboard._loaderId, function () {
                        _dashboard.fillTable( result.items )

                    })
                },
                function (errorData) {
                    app.endAjax(_dashboard._btnFilter, _dashboard._loaderId, function () {
                        app.modals.showError("Error al buscar Rules.", errorData.message );
                    })
                }
            )
        });
    },

    getSearchFilter: function () {
        let uri = $("#inputUri").val();

        let active = $("#inputActivo").prop("checked");
        let inactive = $("#inputInactivo").prop("checked");

        let wichActive;

        if( !(active == true && inactive == true || active == false && inactive == false) ){
            if(active == true){
                wichActive = true
            }

            if( inactive == true){
                wichActive = false
            }
        }


        let data = {
            uri: uri,
            active: wichActive
        };

        return new SearchRuleFilter().loadJson( data );
    },

    fillTable: function (data) {
        let table = $("#table-filter-rule");
        let tableBody = table.find("tbody");
        tableBody.empty();

        if(data.length == 0){
            tableBody.append( templater.TEMPLATE_RULE_TABLE_ROW_EMPTY );
            return;
        }

        $.each(data, function (index, item) {
            let templateData = {
                "__ID__": item.id,
                "__URI__": item.uri,
                "__DESCRIPTION__": item.description,
                "__ACTIVE__": item.active,
                "__ACTIVE_STRING__": (item.active ? "Si" : "No"),
                "__PRIORITY__": item.priority,
                "__TYPE__": item.sourceType,
                "__DATA__": item.data
            };

            let newRow = templater.getFilledTemplate( templateData, templater.TEMPLATE_RULE_TABLE_ROW );

            tableBody.append( newRow );
        });

        _dashboard.setTableActions();
        app.startTooltips();
    },

    setTableActions : function () {
        let table = $("#table-filter-rule");
        let tableBody = table.find("tbody");

        let editActions = tableBody.find("td .bi-pencil-square");
        let deleteActions = tableBody.find("td .bi-x-circle");
        let activateActions = tableBody.find("td .bi-hand-thumbs-up-fill");
        let deactivateActions = tableBody.find("td .bi-hand-thumbs-down-fill");

        editActions.bind("click", _dashboard.tableActions.edit );
        deleteActions.bind("click", _dashboard.tableActions.delete );
        activateActions.bind("click", _dashboard.tableActions.activate );
        deactivateActions.bind("click", _dashboard.tableActions.deactivate );

    },

    tableActions : {
        edit : function (e) {
            e.preventDefault();
            let tr = $(this).parents("tr");

            let ruleData = _dashboard.collectRuleDataFromTR( tr )

            _saveRule.clearForm();
            _saveRule.loadForm( ruleData );

            app.navToSaveRule();
        },
        delete: function (e) {
            e.preventDefault();

            let tr = $(this).parents("tr");
            let ruleData = _dashboard.collectRuleDataFromTR( tr );
            let id = ruleData.id;
            let btnId = "delete-" + id;

            app.modals.promtModal("Delete Rule", "Esta por eliminar la Rule: Uri: " + ruleData.uri + " - Priority: " + ruleData.priority, function(){
                app.startAjax(btnId, _dashboard._loaderId, function () {
                    app.apiClient.deleteRule(id, function (data) {
                        app.endAjax( btnId, _dashboard._loaderId, function () {
                            app.modals.closeDialog(function () {
                                _dashboard._onFilterStart();
                                app.modals.showSuccess("Delete Rule", "La Rule se ha eliminado exitosamente.", function () {
                                    app.modals.closeDialog(function () {
                                    });

                                });
                            });
                        });
                    }, function (errorData) {
                        app.endAjax(btnId, _dashboard._loaderId, function () {
                            app.modals.showError("Error al activar la Rule.", errorData.message );
                        })
                    })
                })
            });
        },
        activate: function (e) {
            e.preventDefault();

            let id = $(this).parents("tr").attr("data-id");

            let btnId = "activate-" + id;
            app.startAjax(btnId, _dashboard._loaderId, function () {
                app.apiClient.enableRule(id, function (data) {
                    app.endAjax( btnId, _dashboard._loaderId, function () {
                        app.modals.showSuccess("Activate Rule", "La Rule se ha activado exitosamente.", function () {
                            _dashboard._onFilterStart();
                            app.modals.closeDialog();
                        });
                    });
                }, function (errorData) {
                    app.endAjax(btnId, _dashboard._loaderId, function () {
                        app.modals.showError("Error al activar la Rule.", errorData.message );
                    })
                })
            })
        },
        deactivate: function (e) {
            e.preventDefault();
            let id = $(this).parents("tr").attr("data-id");

            let btnId = "deactivate-" + id;
            app.startAjax(btnId, _dashboard._loaderId, function () {
                app.apiClient.disableRule(id, function (data) {
                    app.endAjax( btnId, _dashboard._loaderId, function () {
                        app.modals.showSuccess("Deactivate Rule", "La Rule se ha desactivado exitosamente.", function () {
                            _dashboard._onFilterStart();
                            app.modals.closeDialog();
                        });

                    });
                }, function (errorData) {
                    app.endAjax(btnId, _dashboard._loaderId, function () {
                        app.modals.showError("Error al desactivar la Rule.", errorData.message );
                    })
                })
            })
        }
    },

    collectRuleDataFromTR : function (tr) {
        let id = tr.attr("data-id");
        let uri = tr.find("td[data-uri]").attr("data-uri");
        let description = tr.find("i[data-description]").attr("data-description");
        let active = tr.find("td[data-active]").attr("data-active");
        let priority = tr.find("td[data-priority]").attr("data-priority");
        let sourceType = tr.find("td[data-sourceType]").attr("data-sourceType");
        let data = tr.find("td[data-data]").attr("data-data");

        let ruleData = {
            id: id,
            uri: uri,
            description: description,
            active: active,
            priority: priority,
            sourceType: sourceType,
            data: data
        };

        return ruleData;
    }
};