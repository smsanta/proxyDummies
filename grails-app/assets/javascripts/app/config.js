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
        }
    }
};
