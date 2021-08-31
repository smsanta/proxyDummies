class Rule extends DummyObject{

    constructor( loadJson = {} ){
        super();

        this.id ="";
        this.uri="";
        this.priority="";
        this.sourceType="";
        this.data="";
        this.active="";
        this.description="";
        this.requestCondition="";
        this.requestConditionActive="";
        this.method="";
        this.serviceType="";
        this.responseStatus="";
        this.responseExtraHeaders="";
        this.includeDefaultContentType="";

        this.loadJson( loadJson );
    }
}