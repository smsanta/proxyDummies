class AbstractPaginatedFilter extends AbstractFilter{

    constructor( loadJson = {} ){
        super();
        this.page = 1;
        this.pageSize = 10;

        this.loadJson( loadJson );
    }

}