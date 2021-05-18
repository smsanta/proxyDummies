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
                    let parsedJson = undefined;
                    try {
                        parsedJson = JSON.parse( plainJson )
                    }catch (e) {
                        app.endAjax(btnId, _dashboard._loaderId, function () {
                            app.modals.showError("No se pudo importar la Rule.", "JSON inválido.");
                        });

                        return;
                    }


                    app.apiClient.importRule( parsedJson, function () {
                        app.endAjax(btnId, _dashboard._loaderId, function () {
                            app.modals.showSuccess("Rule Import", "La Rule se importo exitosamente.", function () {
                                _dashboard._onFilterStart();
                             });
                        })
                    },function (errorData) {
                        app.endAjax(btnId, _dashboard._loaderId, function () {
                           app.modals.showError("Error al importar la Rule.", errorData.message);
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
                dataString = htmlGenerator.icons.server("text-blue","Ver Data", {
                    id: "show-body-" + item.id,
                    "data-id": item.id,
                    "data-show-event": "showRuleBodyData",
                    action: "watch"
                });
            }

            if( item.sourceType == "FILE" ){
                dataString = htmlGenerator.icons.fileCode("", item.data, {
                    id: "show-body-" + item.id,
                    "data-id": item.id,
                    "data-show-event": "showRuleBodyData",
                    action: "watch"
                });
            }

            let hasExtraHeaders = !(item.responseExtraHeaders === "");

            console.log(item.responseExtraHeaders + " -- " + hasExtraHeaders + " --- " + item.id);
            let headersIconClass = ( hasExtraHeaders ? "bi-node-plus-fill text-green action-icon" : "bi-node-plus action-icon");
            let headersIconTooltip = ( hasExtraHeaders ? "Click Para Ver" : "No tiene." );
            let headersIcon = htmlGenerator.icons.any(headersIconClass, headersIconTooltip, {
                id: item.id,
                "data-id": item.id,
                "data-show-event": (hasExtraHeaders ? "showRuleExtraHeaders" : ""),
                action: "watch"
            });

            let activeIconClass = (item.active ? "bi-check-circle-fill text-green action-icon" : "bi-x-circle-fill text-red action-icon");
            let activeIcon = htmlGenerator.icons.any(activeIconClass, "Rule " + (item.active ? "Activa" : "Inactiva"), {
                id: item.id,
                "data-id": item.id,
                action: "promptSwitchState"
            });

            let hideIcon = (item.description == "" ? "d-none" : "");
            let descriptionIcon = htmlGenerator.icons.any( ("bi-info-circle-fill action-icon text-blue " + hideIcon),  item.description);

            let requestConditionActiveIconClass = (item.requestConditionActive ? "bi-check-circle-fill text-green action-icon" : "bi-x-circle-fill text-red action-icon");
            let requestConditionActiveIcon = htmlGenerator.icons.any(requestConditionActiveIconClass, (item.requestConditionActive ? "Si" : "No"), {
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
                "__SERVICE__": [item.serviceType, item.method, item.responseStatus].join("::"),
                "__HEADERS__": headersIcon,
                "__PRIORITY__": item.priority,
                "__TYPE__": dataString,
                "__DATA__": item.data,
                "__REQUEST_CONDITION__": requestCondition
            };

            let newRow = templater.getFilledTemplate( templateData, templater.TEMPLATE_RULE_TABLE_ROW );

            tableBody.append( newRow );
        });

        _dashboard.setTableActions();
        app.startTooltips();
    },

    showRuleExtraHeaders : function(id){
        let rowData = _dashboard.getRule(id);
        let dataPopup = '<textarea class="w-100 h-100 border-0" disabled>' + Util.escapeXml( rowData.responseExtraHeaders ) + '</textarea>';
        app.modals.showPopup("Headers from -> " + rowData.uri + " <br> Priority: " + rowData.priority,  dataPopup, function () {
            app.modals.closeDialog();
        });
    },

    showRuleBodyData : function(id){
        let rowData = _dashboard.getRule(id);

        let popupTitle = "Data from -> " + rowData.uri;

        if( rowData.sourceType == "FILE" ){
            let copyPathIcon = htmlGenerator.icons.any("bi-files text-blue c-pointer d-none", "Copiar al portapapeles", {
                id: "popup-copy-path",
                "data-path": rowData.data
            });

            popupTitle += "<p class='popup-show-rule-data'> Path: " + rowData.data + copyPathIcon + "</p>";
        }

        if ( _dashboard._rule_data_cache[id] !== undefined ){
            let dataPopup = '<textarea class="w-100 h-100 border-0 disabled">' + Util.escapeXml( _dashboard._rule_data_cache[id] ) + '</textarea>';
            app.modals.showPopup(popupTitle,  dataPopup, function () {
                app.modals.closeDialog();
            });

            $("#popup-copy-path").unbind("click").bind("click", function () {
                console.log("Copy -> " + $(this).attr("data-path") );
                Util.copyToClipboard( $(this).attr("data-path") )
            });
        } else {
            let btnId = "show-body-" + id;
            app.startAjax(btnId, _dashboard._loaderId, function () {
                app.apiClient.getRuleDatabaseBody(id, function (result) {
                        _dashboard._rule_data_cache[id] = result;
                        app.endAjax( btnId, _dashboard._loaderId, function () {
                            let dataPopup = '<textarea class="w-100 h-100 border-0" disabled>' + Util.escapeXml(result) + '</textarea>';
                            app.modals.showPopup(popupTitle,  dataPopup, function () {
                                app.modals.closeDialog();
                            });
                            $("#popup-copy-path").unbind("click").bind("click", function () {
                                console.log("Copy -> " + $(this).attr("data-path") );

                                Util.copyToClipboard( $(this).attr("data-path") )
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
        let copyFilePath = tableBody.find('td i[action=copyFilePath]');


        editActions.bind("click", _dashboard.tableActions.edit );
        deleteActions.bind("click", _dashboard.tableActions.delete );
        activateActions.bind("click", _dashboard.tableActions.activate );
        deactivateActions.bind("click", _dashboard.tableActions.deactivate );
        showActions.bind("click", _dashboard.tableActions.show );
        showRequestCondition.bind("click", _dashboard.tableActions.showRequestCondition );
        exportActions.bind("click", _dashboard.tableActions.export );
        copyFilePath.bind("click", _dashboard.tableActions.copyFilePath );
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
            let btnId = $(this).attr("id");
            let id = tr.attr("data-id");
            let dataShowEvent = $(this).attr("data-show-event");

            console.log(dataShowEvent);
            if( dataShowEvent != "" ){
                _dashboard[dataShowEvent](id);
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
        },

        copyFilePath: function (e) {
            let tr = $(this).parents("tr");
            let rowData = _dashboard.collectRuleDataFromTR(tr);

            Util.copyToClipboard( rowData.data );
            app.modals.showSuccess("Rule data file ", "El path al archivo se ha copiado al portapaples!", function () {
                app.modals.closeDialog();
            });

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