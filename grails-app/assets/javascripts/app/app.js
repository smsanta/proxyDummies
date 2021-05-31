/**
 * Defining JS Application
 */
var app = {
    config : undefined,
    modals : undefined,
    apiClient : undefined,

    /**
     * Static error messages.
     */
    messages : {
        COULD_NOT_GET_CONTENT_PAGE: "Ha ocurrido un error al cargar la p\u00e1gina.",
    },

    _access_token : "",

    views: [],

    /**
	 * Initializes the app.
	 */
	initialize : function(){
        config.baseUrl = app.getBaseUrl();

        app.config = config;
        app.modals = modals;
        app.apiClient = apiClient;

        app.views = [ _dashboard, _saveRule, _configuration, _saveEnvironment ];

        app.load();
	},

	/**
	 * Initializes page behavior and visual settings.
	 */
	load : function(){
        app.log("loading application resoureces.");
        app.initCommonVisuals();
        app.initViews();

        _dashboard._onFilterStart();
	},

    initViews: function(){
	    $.each( app.views, function (index, viewInitialize ){
            viewInitialize.initialize();
        });
    },

    /**
     * Collects and return the current Query String params.
     *
     * @returns {JSON objects}
     */
    getUrlVars : function() {
        var vars = {};
        var href = window.location.href;

        if( href.indexOf('?') >= 0 ){
            var plainQSParams =  href.substring(href.indexOf('?') + 1, href.length);
            var hashes = plainQSParams.split('&');

            for(var i = 0; i < hashes.length; i++){
                var splitParam = hashes[i].split('=');
                vars[splitParam[0]] = splitParam[1];
            }
        }

        return vars;
    },

    initCommonVisuals : function(){
        app.startTooltips();
    },

    startTooltips : function(){
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl)
        });
    },

    getBaseUrl : function(){
        return window.location.protocol + "//" + window.location.host  + "/proxyDummies";
    },

    /**
     * Returns the default page.
     *
     * @return {string}
     */
    getDefaultPage : function(){
        return window.location.protocol + "//" + window.location.host;
    },

    log: function (message) {
        if( config.developmentMode ){
            console.log( message )
        }
    },

    startAjax: function(btnId, loaderId, callback, opts ){
        $(btnId).addClass("disabled");

        opts = validator.getValueOrDefault(opts, {});

        if( validator.isObject(opts.disable) ){
            $.each(opts.disable, function (idx, elem) {
                $(elem).addClass("disabled");
            })
        }

        var loaderConfig = $.extend({
            method : "fadeIn",
            speed : "fast"
        }, validator.getValueOrDefault(opts.loader, {}));

        $(loaderId)[ loaderConfig.method ]( loaderConfig.speed, callback);
    },

    /**
     *
     * @param btnId - The object that starts the ajax event. (Require for disabling it while ajax is occuring)
     * @param loaderId - The id of the oject where the loader is being shown.
     * @param opts - {
     *     disable : [] <--- An Array with extra elements to disable
     *     loader : {
     *         method : "fadeIn", <-- By Default
     *         speed : "fast" <-- By Default
     *     }
     * }
     * @param callback
     */
    endAjax: function( btnId, loaderId, callback, opts ){
        $(btnId).removeClass("disabled");

        opts = validator.getValueOrDefault(opts, {});

        if( validator.isObject(opts.enable) ){
            $.each(opts.enable, function (idx, elem) {
                $(elem).removeClass("disabled");
            })
        }

        var loaderConfig = $.extend({
            method : "fadeOut",
            speed : "fast"
        }, validator.getValueOrDefault(opts.loader, {}));

        $(loaderId)[ loaderConfig.method ]( loaderConfig.speed, callback);
    },

    navToDashboard: function () {
        $("#v-pills-home-tab").tab("show")
    },

    navToSaveRule: function () {
        $("#v-pills-save-rule-tab").tab("show")
    },

    navToConfig: function () {
        $("#v-pills-settings-tab").tab("show")
    }
};