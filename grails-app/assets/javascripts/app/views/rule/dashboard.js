var _dashboard = {

    _btnFilter: "#btn-search",
    _btnImport: "#btn-import-rule",
    _loaderId: "#loader-dashboard",
    _rule_search_cache: {},
    _rule_data_cache:{},

    initialize: function () {
        _dashboard.setFilterEvent();
        _dashboard.setImportEvent();

    },

    setFilterEvent : function () {
        let btnFilter = $( _dashboard._btnFilter );

        btnFilter.unbind().bind("click", function (e) {
            e.preventDefault();
            _dashboard._onFilterStart();
        })
    },

    setImportEvent : function () {
        let btnImport = $( _dashboard._btnImport );

        btnImport.unbind().bind("click", function (e) {
            e.preventDefault();

            let popupTextAreaTmpId = "tmp-ta-" + Date.now();
            let dataPopup = '<textarea id="'+ popupTextAreaTmpId +'" class="w-100 h-100 border-0"></textarea>';

            app.modals.promtModal("Import Rule -> Pegar JSON raw", dataPopup ,
                function () {
                let btnId = $(this).attr("id");
                app.startAjax(btnId, _dashboard._loaderId, function () {
                    let plainJson = $("#" + popupTextAreaTmpId).val();
                    app.apiClient.importRule( plainJson, function () {
                        app.endAjax(btnId, _dashboard._loaderId, function () {
                            //app.modals.closeDialog(function () {
                                app.modals.showSuccess("Rule Import", "La Rule se importo exitosamente.", function () {
                                    _dashboard._onFilterStart();
                             });
                         //});
                    })
                   },function (errorData) {
                       app.endAjax(btnId, _dashboard._loaderId, function () {
                           app.modals.closeDialog(function () {
                                app.modals.showError("Error al importar la Rule.", errorData.message);
                           });

                       })
                   })
                });
                app.modals.closeDialog();
            },
                function () {
                    app.modals.closeDialog();
            });
        })
    },

    _onFilterStart : function(){
        let searchFilter = _dashboard.getSearchFilter();

        app.startAjax( _dashboard._btnFilter, _dashboard._loaderId, function () {
            app.apiClient.searchRules(searchFilter,
                function (result) {
                    app.endAjax( _dashboard._btnFilter, _dashboard._loaderId, function () {
                        _dashboard._rule_search_cache = result.items;
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
            let dataString = item.data;

            if( item.sourceType == "DATABASE" ){
                dataString = htmlGenerator.icons.eyeFill("text-blue","Ver Data", {
                    id: item.id,
                    "data-id": item.id,
                    action: "watch"
                });
            }

            let activeIconClass = (item.active ? "bi-check-circle-fill text-green action-icon" : "bi-x-circle-fill text-red action-icon");
            let activeIcon = htmlGenerator.icons.any(activeIconClass, "Ver Data", {
                id: item.id,
                "data-id": item.id,
                action: "watch"
            });

            let hideIcon = (item.description == "" ? "d-none" : "");
            let descriptionIcon = htmlGenerator.icons.any( ("bi-info-circle-fill action-icon text-blue " + hideIcon),  item.description);

            let requestConditionActiveIconClass = (item.requestConditionActive ? "bi-check-circle-fill text-green action-icon" : "bi-x-circle-fill text-red action-icon");
            let requestConditionActiveIcon = htmlGenerator.icons.any(requestConditionActiveIconClass, "", {
                id: item.id,
                "data-id": item.id
            });


            hideIcon = (item.requestConditionActive === true ? "" : "d-none");
            let requestConditionIcon = htmlGenerator.icons.eyeFill("text-blue " + hideIcon,"Ver Condition", {
                id: item.id,
                "data-id": item.id,
                action: "watch-condition"
            });

            let arrowIcon = htmlGenerator.icons.any("bi-arrow-right action-icon " + hideIcon,"", {});
            let requestCondition = requestConditionActiveIcon + arrowIcon + requestConditionIcon;

            let templateData = {
                "__ID__": item.id,
                "__URI__": item.uri,
                "__DESCRIPTION__": descriptionIcon,
                "__ACTIVE__": item.active,
                "__ACTIVE_STRING__": activeIcon,
                "__PRIORITY__": item.priority,
                "__TYPE__": item.sourceType,
                "__DATA__": item.data,
                "__DATA_STRING__": dataString,
                "__REQUEST_CONDITION__": requestCondition

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

        let editActions = tableBody.find("td i[action=edit]");
        let deleteActions = tableBody.find("td i[action=delete]");
        let activateActions = tableBody.find("td i[action=activate]");
        let deactivateActions = tableBody.find("td i[action=deactivate]");
        let showActions = tableBody.find('td i[action=watch]');
        let showRequestCondition = tableBody.find('td i[action=watch-condition]');
        let exportActions = tableBody.find('td i[action=export]');

        editActions.bind("click", _dashboard.tableActions.edit );
        deleteActions.bind("click", _dashboard.tableActions.delete );
        activateActions.bind("click", _dashboard.tableActions.activate );
        deactivateActions.bind("click", _dashboard.tableActions.deactivate );
        showActions.bind("click", _dashboard.tableActions.show );
        showRequestCondition.bind("click", _dashboard.tableActions.showRequestCondition );
        exportActions.bind("click", _dashboard.tableActions.export );

    },

    tableActions : {
        edit : function (e) {
            e.preventDefault();
            let tr = $(this).parents("tr");

            let ruleData = _dashboard.collectRuleDataFromTR( tr );

            _saveRule.clearForm();
            let id = ruleData.id;
            let btnId = $(this).attr("id");

            if ( _dashboard._rule_data_cache[id] !== undefined ){
                ruleData.data = _dashboard._rule_data_cache[id];
                _saveRule.loadForm( ruleData );
                app.navToSaveRule();
            } else {
                app.startAjax(btnId, _dashboard._loaderId, function () {
                    app.apiClient.getRuleDatabaseBody(id, function (result) {
                            _dashboard._rule_data_cache[id] = result;
                            app.endAjax( btnId, _dashboard._loaderId, function () {
                                ruleData.data = _dashboard._rule_data_cache[id];
                                _saveRule.loadForm( ruleData );
                                app.navToSaveRule();
                            })
                        },
                        function (errorData) {
                            app.endAjax(btnId, _dashboard._loaderId, function () {
                                app.modals.showError("Error al obtener la data de la rule.", errorData.message );
                            })
                        });
                } );
            }


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
        },

        show: function (e) {
            e.preventDefault();
            let tr = $(this).parents("tr");
            let rowData = _dashboard.collectRuleDataFromTR(tr);

            let btnId = $(this).attr("id");

            let id = rowData.id;

            if ( _dashboard._rule_data_cache[id] !== undefined ){
                let dataPopup = '<textarea class="w-100 h-100 border-0 disabled">' + Util.escapeXml( _dashboard._rule_data_cache[id] ) + '</textarea>';
                app.modals.showPopup("Data from -> " + rowData.uri,  dataPopup, function () {
                    app.modals.closeDialog();
                });
            } else {
                app.startAjax(btnId, _dashboard._loaderId, function () {
                    app.apiClient.getRuleDatabaseBody(id, function (result) {
                            _dashboard._rule_data_cache[id] = result;
                            app.endAjax( btnId, _dashboard._loaderId, function () {
                                let dataPopup = '<textarea class="w-100 h-100 border-0" disabled>' + Util.escapeXml(result) + '</textarea>';
                                app.modals.showPopup("Data from -> " + rowData.uri,  dataPopup, function () {
                                    app.modals.closeDialog();
                                });
                            })
                        },
                        function (errorData) {
                            app.endAjax(btnId, _dashboard._loaderId, function () {
                                app.modals.showError("Error al desactivar la Rule.", errorData.message );
                            })
                        });
                } );
            }
        },

        showRequestCondition:  function (e) {
            e.preventDefault();
            let tr = $(this).parents("tr");
            let rowData = _dashboard.collectRuleDataFromTR(tr);

            let id = rowData.id;

            let dataPopup = '<textarea disabled class="w-100 h-100 border-0">' + rowData.requestCondition + '</textarea>';
            app.modals.showPopup("Request Condition from -> " + rowData.uri, dataPopup, function () {
                app.modals.closeDialog();
            });
        },

        export: function (e) {
            let tr = $(this).parents("tr");
            let rowData = _dashboard.collectRuleDataFromTR(tr);
            let id = rowData.id;
            let btnId = $(this).attr("id");

            app.startAjax(btnId, _dashboard._loaderId, function () {
                app.apiClient.exportRule(id, function (result) {
                        app.endAjax( btnId, _dashboard._loaderId, function () {
                            Util.copyToClipboard( result );
                            app.modals.showSuccess("Rule Export -> " + rowData.uri + " - Priority -> " + rowData.priority,  "La rule se ha copiado al portapaples!", function () {
                                app.modals.closeDialog();
                            });
                        })
                    },
                    function (errorData) {
                        app.endAjax(btnId, _dashboard._loaderId, function () {
                            app.modals.showError("Error al generar el export de la Rule.", errorData.message );
                        })
                    });
            } );
        }
    },

    collectRuleDataFromTR : function (tr) {
        let id = tr.attr("data-id");

        return _dashboard.getRule(id);
    },

    getRule : function (id) {
        let rule = undefined;

        $.each( _dashboard._rule_search_cache, function (index, eachRule) {
            if( eachRule.id == id ){
                rule = eachRule;
            }
        });

        return rule;
    }
};