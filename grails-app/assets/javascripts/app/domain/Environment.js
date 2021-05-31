class Environment extends DummyObject{

    constructor( loadJson = {} ){
        super();

        this.id ="";
        this.name="";
        this.url="";
        this.uriPrefix="";

        this.loadJson( loadJson );
    }
}