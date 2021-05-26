class Environment extends DummyObject{

    constructor( loadJson = {} ){
        super();

        this.id ="";
        this.name="";
        this.url="";
        this.urlPrefix="";

        this.loadJson( loadJson );
    }
}