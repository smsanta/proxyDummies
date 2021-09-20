let _requestLogs = {

    _loaderId: "#loader-configuration",
    _contentId: "#request-logs",
    _headerRequestLogsSelector: "#head-switch-request-logs",
    _logTableSelector: "#table-request-logs",
    _realTimeUpdateInterval: undefined,
    _realTimeUpdateIntervalTimeRemaining: undefined,
    _realTimeUpdateIntervalTimeout: 5000,
    _searchLogs : [],
    _emptyBodyMessage: "<< PD >> (Original body was empty) << PD >>",

    initialize: function () {
        _requestLogs.startHeaderLogsEvent();
        _requestLogs.setIconEvents();
    },

    startHeaderLogsEvent: function () {
        $( _requestLogs._headerRequestLogsSelector ).change(function() {
            let checked = this.checked;
            let container = $( _requestLogs._contentId );

            let switcher = checked ? "0px" : "100%";
            container.css("right", switcher);

            _requestLogs.realTimeListenerSwitcher( checked );
        });

        _requestLogs._realTimeUpdateInterval = new Interval( _requestLogs.loadLatestRequestLog, _requestLogs._realTimeUpdateIntervalTimeout );

        _requestLogs._realTimeUpdateIntervalTimeRemaining = new Interval(function () {
            let reloadIcon = $("#request-log-loader");

            reloadIcon.removeClass("bi-reception-0")
                .removeClass("bi-reception-1")
                .removeClass("bi-reception-2")
                .removeClass("bi-reception-3")
                .removeClass("bi-reception-4")
                .removeClass("text-red")
                .removeClass("text-green");
            if( _requestLogs._realTimeUpdateInterval.isRunning() ){
                let timeLeft = Math.floor( _requestLogs._realTimeUpdateInterval.getTimeLeft() / 1000 );
                reloadIcon.addClass("text-green bi-reception-" + (4 - timeLeft ));

            } else {
                reloadIcon.addClass("text-red bi-reception-0" );
            }
        }, 1000);
    },

    setIconEvents: function () {
        $("#request-log-autoupdate").unbind("click").bind("click", _requestLogs.onRealtimeUpdateClick );
        $("#request-log-hide").unbind("click").bind("click", _requestLogs.onHideClick );
    },

    onRealtimeUpdateClick: function () {
        let icon = $(this);
        let isActive = icon.attr("data-active") === "true";

        _requestLogs.realTimeListenerSwitcher( !isActive )
    },

    onHideClick: function () {
        $( _requestLogs._headerRequestLogsSelector ).prop("checked", false).change();
    },

    loadLatestRequestLog: function(){
        let searchFilter = new SearchRequestLogFilter();
        searchFilter.maxResults = $("#request-log-autoupdate-results").val();
        apiClient.searchRequestLogs( searchFilter, function (logs) {
            _requestLogs.fillRequestTable( logs.items );
        }, function (response) {
            _requestLogs.realTimeListenerSwitcher( false );
            _requestLogs.fillRequestTable( [] );
            app.modals.showError(apiClient.messages.logs.search.error, response.message );
        });
    },

    fillRequestTable: function( items ){
        let logsTable = $( _requestLogs._logTableSelector );
        let tableBody = logsTable.find( "tbody" );

        tableBody.empty();
        if ( items.length ){
            _requestLogs._searchLogs = {};
            $.each(items, function (index, item) {
                _requestLogs._searchLogs[item.id] = item;

               let forwarded = !validator.isObject(item.rule);
               tableBody.append(
                   templater.getFilledTemplate(
                   {
                       "__ID__" : item.id,
                       "__DATE__" : item.eventDate,
                       "__STATUS__" : item.responseStatus,
                       "__URI__" : item.uri,
                       "__METHOD__" : item.requestType,
                       "__DISPLAY_RULE__": ( forwarded ? "d-none" : "" ),
                       "__DISPLAY_FORWARD__": (forwarded ? "" : "d-none"),
                       "__FORWARD_TOOLTIP__": (forwarded ? ("Forwarded to: " + item.urlDestination) : "")
                   },
                   templater.TEMPLATE_LOGS_TABLE_ROW )
               )
            });

            app.startTooltips();
            _requestLogs.setTableActions();
        } else {
            let plainDate = Util.getDateHMS();
            tableBody.append( templater.getFilledTemplate( {"__DATE__" : plainDate}, templater.TEMPLATE_LOGS_TABLE_ROW_EMPTY ) )
        }
    },

    setTableActions : function () {
        let logsTable = $( _requestLogs._logTableSelector );
        let tableBody = logsTable.find( "tbody" );

        let viewRuleAction = tableBody.find("td i[action=view-rule]");
        let viewRequestBodyAction = tableBody.find("td i[action=view-request-body]");
        let viewRequestHeaderAction = tableBody.find("td i[action=view-request-header]");
        let viewResponseBodyAction = tableBody.find("td i[action=view-response-body]");
        let viewResponseHeaderAction = tableBody.find("td i[action=view-response-header]");

        viewRuleAction.bind("click", _requestLogs.tableActions.viewRule);
        viewRequestBodyAction.bind("click", _requestLogs.tableActions.viewRequestBody);
        viewRequestHeaderAction.bind("click", _requestLogs.tableActions.viewRequestHeader);
        viewResponseBodyAction.bind("click", _requestLogs.tableActions.viewResponseBody);
        viewResponseHeaderAction.bind("click", _requestLogs.tableActions.viewResponseHeader);
    },

    realTimeListenerSwitcher: function ( newState ) {
        let currentState = _requestLogs._realTimeUpdateInterval.isRunning();
        let stateUpdate = newState === undefined ? !currentState : newState;

        let icon = $("#request-log-autoupdate");

        if( stateUpdate ){
            icon.attr('class', '').attr('class', 'bi-stop-circle text-red action-icon');
            $("#main-content").removeClass("h-100");
            _requestLogs.loadLatestRequestLog();
            _requestLogs._realTimeUpdateInterval.start();
            _requestLogs._realTimeUpdateIntervalTimeRemaining.start();
        }else {
            $("#main-content").addClass("h-100");
            icon.attr('class', '').attr('class', 'bi-play-circle text-green action-icon');
            _requestLogs._realTimeUpdateInterval.stop();
            _requestLogs._realTimeUpdateIntervalTimeRemaining.stop().trigger();
        }

        icon.attr("data-active", stateUpdate);
    },

    getRowId: function(element){
        return $(element).parents("tr").attr("data-id");
    },

    tableActions: {
        viewRule: function () {
            let id = _requestLogs.getRowId( this );
            let logItem = _requestLogs._searchLogs[id];

            _requestLogs.realTimeListenerSwitcher( false );
            app.navToDashboard();
            _dashboard._doSearch( new SearchRuleFilter({id: logItem.rule.id} ) )
        },

        viewRequestBody: function () {
            let id = _requestLogs.getRowId( this );
            let logItem = _requestLogs._searchLogs[id];

            _requestLogs.realTimeListenerSwitcher( false );

            let title = "Request Body: [" + logItem.eventDate + "] - " + logItem.uri;
            let content = validator.getValueOrDefault(logItem.requestBody, _requestLogs._emptyBodyMessage);
            let dataPopup = htmlGenerator.tag.textarea({
                disabled: "",
                class: "w-100 h-100 border-0",
                text: content
            });

            app.modals.showPopup(title, dataPopup, function () {
                app.modals.closeDialog();
            });
        },

        viewRequestHeader: function () {
            let id = _requestLogs.getRowId( this );
            let logItem = _requestLogs._searchLogs[id];

            _requestLogs.realTimeListenerSwitcher( false );

            let title = "Request Headers: [" + logItem.eventDate + "] - " + logItem.uri;

            let content = validator.getValueOrDefault( logItem.requestHeaders , "");

            if( content ){
                content = JSON.stringify(JSON.parse(content),null,2);
            }

            let dataPopup = htmlGenerator.tag.textarea( {
                disabled: "",
                class: "w-100 h-100 border-0",
                text: content
            });

            app.modals.showPopup(title, dataPopup, function () {
                app.modals.closeDialog();
            });
        },

        viewResponseBody: function () {
            let id = _requestLogs.getRowId( this );
            let logItem = _requestLogs._searchLogs[id];

            _requestLogs.realTimeListenerSwitcher( false );

            let title = "Response Body: [" + logItem.eventDate + "] - " + logItem.uri;

            if ( validator.isObject(logItem.rule) ){
                    _dashboard.showRuleBodyData( logItem.rule.id, { title: title } )
            } else {
                let content = validator.getValueOrDefault(logItem.responseBody, _requestLogs._emptyBodyMessage);
                let dataPopup = htmlGenerator.tag.textarea({
                    disabled: "",
                    class: "w-100 h-100 border-0",
                    text: content
                });

                app.modals.showPopup(title, dataPopup, function () {
                    app.modals.closeDialog();
                });
            }
        },

        viewResponseHeader: function () {
            let id = _requestLogs.getRowId( this );
            let logItem = _requestLogs._searchLogs[id];

            _requestLogs.realTimeListenerSwitcher( false );

            let title = "Response Headers: [" + logItem.eventDate + "] - " + logItem.uri;
            let content = validator.getValueOrDefault( logItem.responseHeaders , "");

            if( content ){
                content = JSON.stringify(JSON.parse(content),null,2);
            }

            let dataPopup = htmlGenerator.tag.textarea({
                disabled: "",
                class: "w-100 h-100 border-0",
                text: content
            });

            app.modals.showPopup(title, dataPopup, function () {
                app.modals.closeDialog();
            });
        }
    },

};