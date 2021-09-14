class SearchRequestLogFilter extends AbstractFilter{

    constructor( loadJson = {} ){
        super(loadJson);

        this.maxResults = "";
    }
}