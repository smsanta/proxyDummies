class SearchRuleFilter extends AbstractFilter{

    constructor( loadJson = {} ){
        super(loadJson);

        this.id = "";
        this.uri = "";
        this.active = "";

        this.loadJson(loadJson);
    }
}