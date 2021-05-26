var config = {
    baseUrl : window.location.host,
    developmentMode : false,

    pages : {
        dashboard : {
            title : "Vista General"
        }
    },

    view : {
        dashboard : {
            defaulSearchOptions : {
                limit : 30,
                offset : 0,
                sort_by : "description,asc"
            },

            enabledPaymentMethods : ['ticket', 'atm'],
        },

        saveRule : {
            formDefaults : {
                id: "",
                uri: "",
                priority: "",
                sourceType: "DATABASE",
                data: "",
                active: false,
                description: "",
                requestCondition: "",
                requestConditionActive: false,
                method: "POST",
                serviceType: "SOAP",
                responseStatus: "200",
                responseExtraHeaders: ""
            }
        }
    }
};
