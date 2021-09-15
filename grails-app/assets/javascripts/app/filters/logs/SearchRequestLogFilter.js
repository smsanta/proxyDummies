class SearchRequestLogFilter extends AbstractFilter{

    constructor( loadJson = {} ){
        super(loadJson);

        this.maxResults = "";

        this.loadJson( loadJson );
    }
}