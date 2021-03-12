package proxydummies.abstracts

import proxydummies.filters.FilterResult
import proxydummies.filters.ResultFilterStatistics

abstract class AbstractPaginatedFilter extends AbstractFilter{

    Integer page = 1
    Integer pageSize = 10

    FilterResult result

    /**
     * Returns the index from where the result must start to filter.
     *
     * @return
     */
    Integer getOffset(){
        getInternalPage() * pageSize
    }

    Integer getInternalPage(){
        page - 1
    }

    /**
     * Calculates de last page according to the total of items
     * from the current filter target class.
     * @return
     */
    Integer getLastPage(){
        Integer totalPages = resultCount / pageSize

        if( (resultCount % pageSize) > 0 || totalPages == 0 ){
            totalPages++
        }

        totalPages
    }

    /**
     * Builds statics for the current search.
     * This method requires a search already done b4 usage.
     */
    ResultFilterStatistics buildStatistics(){
        ResultFilterStatistics filterStatistics = ResultFilterStatistics.newInstance()
        filterStatistics.with {
            page = this.page
            pageSize = this.pageSize
            offset = this.getOffset()
            lastPage = this.getLastPage()
            totalResults = this.getResultCount()
        }
        filterStatistics
    }

    FilterResult withCriteria(Closure criteriaClosure){
        result = FilterResult.newInstance()
        result.results = getCriteria().list([max: pageSize, offset: offset], criteriaClosure)
        result.statistics = buildStatistics()
        result
    }

}
