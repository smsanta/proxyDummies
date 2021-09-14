_requestLogs = {

    _loaderId: "#loader-configuration",
    _contentId: "#request-logs",
    _headerRequestLogsSelector: "#head-switch-request-logs",
    _logTableSelector: "#table-request-logs",
    _realTimeUpdateState: false,
    _realTimeUpdateInterval: undefined,
    _realTimeUpdateIntervalTimeout: 5000,

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

    startRealTimeRequestUpdater: function () {
        _requestLogs.loadLatestRequestLog();
        _requestLogs._realTimeUpdateInterval = setInterval(_requestLogs.loadLatestRequestLog, _requestLogs._realTimeUpdateIntervalTimeout);
    },

    loadLatestRequestLog: function(){
        let searchFilter = new SearchRequestLogFilter();
        searchFilter.maxResults = $("#request-log-autoupdate-results").val();
        apiClient.searchRequestLogs( searchFilter, function (logs) {
            _requestLogs.fillRequestTable( logs.items );
        });
    },

    fillRequestTable: function( items ){
        let logsTable = $( _requestLogs._logTableSelector );
        let tableBody = logsTable.find( "tbody" );

        tableBody.empty();
        if ( items.length ){
            $.each(items, function (index, item) {
               tableBody.append(
                   templater.getFilledTemplate(
                       {
                           "__ID__" : item.id,
                           "__DATE__" : item.eventDate,
                           "__STATUS__" : item.responseStatus,
                           "__URI__" : item.uri,
                           "__METHOD__" : item.requestType,
                           "__DISPLAY_RULE_": item.rule
                       },
                       templater.TEMPLATE_LOGS_TABLE_ROW
                   )
               )
            });

            app.startTooltips();
        } else {
            let plainDate = Util.getDateHMS();
            tableBody.append( templater.getFilledTemplate( {"__DATE__" : plainDate}, templater.TEMPLATE_LOGS_TABLE_ROW_EMPTY ) )
        }
    },

    realTimeListenerSwitcher: function ( newState ) {
        let currentState = _requestLogs._realTimeUpdateState;
        let stateUpdate = newState === undefined ? !currentState : newState;

        let icon = $("#request-log-autoupdate");

        _requestLogs._realTimeUpdateState = stateUpdate;

        console.log("Updating state: " + stateUpdate );

        if( stateUpdate ){
            icon.attr('class', '').attr('class', 'bi-stop-circle text-red action-icon');
            _requestLogs.startRealTimeRequestUpdater();
        }else {
            icon.attr('class', '').attr('class', 'bi-play-circle text-green action-icon');
            clearInterval( _requestLogs._realTimeUpdateInterval )
        }

        icon.attr("data-active", stateUpdate);
    }

};