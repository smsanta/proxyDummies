var modals = {

    live : {
        currentModal : "",

        getCurrentModal: function(){
            return $("#" + modals.live.currentModal);
        },

        startAjax: function(callback){
            app.modals.live.disableButtons();
            app.modals.live.showExchangeArea("fast", true, function(){
                app.modals.live.showLoader("fast", callback);
            })
        },

        endAjax: function( message ){
            app.modals.live.enableButtons("close");
            app.modals.live.hideLoader("fast", function () {
                var modal = modals.live.getCurrentModal();
                modal.find(".body-exchange").append( message );
            });
        },

        showLoader : function ( speed, callback ) {
            var modal = modals.live.getCurrentModal();
            var loader = htmlGenerator.icons.loader( true, {}, false );

            var bodyExchange = modal.find(".body-exchange").append( loader );

            loader.slideDown( validator.getValueOrDefault(speed, "slow"), callback);
        },

        hideLoader : function ( speed, callback ) {
            var modal = modals.live.getCurrentModal();
            var bodyExchange = modal.find(".body-exchange");

            var loader = bodyExchange.find(".spinner-border");

            loader.fadeOut( validator.getValueOrDefault(speed, "slow"), function () {
                loader.remove();
                callback();
            });
        },

        showExchangeArea: function(speed, clearContent, callback){
            var modal = modals.live.getCurrentModal();
            var bodyExchange = modal.find(".body-exchange");

            if( validator.getValueOrDefault(clearContent, true) ){
                bodyExchange.empty();
            }

            bodyExchange.slideDown( validator.getValueOrDefault(speed, "slow"),
                validator.getValueOrDefault(callback, $.noop));
        },

        hideExchangeArea: function(speed, clearContent, callback){
            var modal = modals.live.getCurrentModal();
            var bodyExchange = modal.find(".body-exchange");

            if( validator.getValueOrDefault(clearContent, true) ){
                bodyExchange.empty();
            }

            bodyExchange.slideUp( validator.getValueOrDefault(speed, "slow"),
                validator.getValueOrDefault(callback, $.noop));
        },

        enableButtons: function ( byRole ) {
            var modal = modals.live.getCurrentModal();
            var modalFooter = modal.find(".modal-footer");

            if( validator.isObject(byRole) ){
                if( !validator.isArray( byRole ) ){
                    byRole = [byRole];
                }

                $.each( byRole, function (idx, eachRole){
                    modalFooter.find( 'button[role="'+ eachRole +'"]' ).removeClass("disabled");
                })

            } else {
                modalFooter.find("button").removeClass("disabled");
            }
        },

        disableButtons: function ( byRole ) {
            var modal = modals.live.getCurrentModal();
            var modalFooter = modal.find(".modal-footer");

            if( validator.isObject(byRole) ){
                if( !validator.isArray( byRole ) ){
                    byRole = [byRole];
                }

                $.each( byRole, function (idx, eachRole){
                    modalFooter.find( 'button[role="'+ eachRole +'"]' ).addClass("disabled");
                })

            } else {
                modal.find(".modal-footer").find("button").addClass("disabled");
            }

        }
    },

    type : {
        ALERT : "main-alert",
        SUCCESS : "success-modal",
        ERROR : "error-modal"
    },

    templates : {
        BUTTON : '<button type="button" class="btn btn-primary btn-sm"></button>'
    },

    defaults : {
        buttons : {
            accept: {
                label: "Aceptar",
                role: "accept",
                click: function () {
                    modals.closeDialog();
                }
            },
            close: {
                label: "Cerrar",
                role: "close",
                click: function () {
                    modals.closeDialog();
                }
            }
        }
    },

    utilities: {
        buildButton: function (text, classes, attr) {
            text = validator.getValueOrDefault(text, "");
            classes = validator.getValueOrDefault(classes, "");

            var btn = $(modals.templates.BUTTON)
                .addClass( classes )
                .text( text );

            if( validator.isObject(attr) ){
                $.each(attr, function(k, v){
                    btn.attr(k, v);
                });
            }

            return btn;
        }
    },

    /**
     * Displays an Success on screen.
     * @param title
     * @param message
     */
    showSuccess : function(title, message, successCallback){
        var btnAccept = {};
        btnAccept = $.extend( btnAccept, modals.defaults.buttons.accept );

        if( validator.isObject(successCallback) ){
            btnAccept.click = successCallback
        }

        app.modals.showModal({
                title : title,
                body : message
            },
            [ btnAccept ],
            modals.type.SUCCESS
        );
    },

    /**
     * Displays an Success on screen.
     * @param title
     * @param message
     */
    showError : function(title, message, successCallback){
        var btnAccept = {};
        btnAccept = $.extend( btnAccept, modals.defaults.buttons.accept );

        if( validator.isObject(successCallback) ){
            btnAccept.click = successCallback
        }

        app.modals.showModal({
                title : title,
                body : message
            },
            [ btnAccept ],
            modals.type.ERROR
        );
    },

    /**
     * Shows an message and does something after accept.
     *
     * @param title
     * @param message
     * @param successCallback - By default if not given closes the alert.
     */
    showAlert : function(title, message, successCallback){
        var btnAccept = {};
        btnAccept = $.extend( btnAccept, modals.defaults.buttons.accept );

        if( validator.isObject(successCallback) ){
            btnAccept.click = successCallback
        }

        modals.showModal({
               title: title,
               body: message
           },
           [ btnAccept ],
            modals.type.ALERT
       )
    },

    promtModal : function(title, message, acceptCallback, cancelCallback, buttons){
        var btnAccept = {};
        btnAccept = $.extend( btnAccept, modals.defaults.buttons.accept );
        var btnCancel = {};
        btnCancel = $.extend( btnCancel, modals.defaults.buttons.close );

        btnAccept.click = acceptCallback;

        if( validator.isObject(cancelCallback) ){
            btnCancel.click = cancelCallback;
        }

        var allButtons = [ btnCancel, btnAccept ];
        if( validator.isObject(buttons) ){
            $.merge(allButtons, buttons);
        }


        modals.showModal({
                title: title,
                body: message
            },
            allButtons,
            modals.type.ALERT
        )
    },

    /**
     * Closes a message if it was shown.
     *
     * @param callback a function to be called after the modal is closed.
     * @param modalId The selector of the current modal if null or undefined default modal is #modal
     */
    closeDialog : function (callback, modalId) {
        if( !validator.isObject( callback ) ){
            callback = $.noop;
        }
        modalId = validator.getValueOrDefault(modalId, modals.live.currentModal);

        var selectorModalId = "#" + modalId;


        $( selectorModalId ).modal('hide');

        $( selectorModalId ).on('hidden.bs.modal', function (e) {
            callback();
        });
    },

    /**
     * Shows a modal on screen with 2 possible options.
     *
     * @param buttons -> {
     *      accept: {
     *          click: function(){...},
     *          label: "Aceptar",
     *          hidden: false
     *      },
     *      cancel: {
     *          click: function(){...},
     *          label: "Cancelar",
     *      },
     * @param data -> {
     *     title : " Titulo del modal ",
     *     body : " Mensaje del modal "
     * }
     */
    showModal : function( data, buttons, type){
        var modalId = "#" + type;
        var modal = $( modalId );

        modal.find( "." + type + "-title" ).empty().append( validator.getValueOrDefault(data.title, "") );
        modal.find( "." + type + "-body" ).empty().append( validator.getValueOrDefault(data.body, "") );

        var btns = modal.find(".modal-footer");
        var bodyExchange = modal.find(".body-exchange");

        btns.empty();
        bodyExchange.empty().css("display", "none");
        bodyExchange.empty();

        if( validator.isObject( buttons ) ){
            $.each(buttons, function (idx, button) {
                var btnId = type+ "-btn-" + idx;

                var btnAttr = {"id" : btnId };

                if( validator.isObject(button.role) ){
                    btnAttr.role = button.role;
                }

                btns.append( modals.utilities.buildButton( button.label, button.classes, btnAttr ) );

                $('#'+btnId).bind( "click", button.click );

                $( modalId ).on('hidden.bs.modal', function (e) {
                    $( "#" + app.modals.live.currentModal + "-btn-0").click();
                });
            })
        }

        modals.live.currentModal = type;

        modal.modal('show');
    }
}