package proxydummies.abstracts

import proxydummies.filters.FilterResult

abstract class AbstractFilter implements Populable {

    FilterResult result

    /**
     * Returns the total count of a query.
     * This method requires a search already done b4 usage.
     *
     * @return
     */
    Integer getResultCount(){
        result?.results?.totalCount != null ? result?.results?.totalCount : 0
    }

    FilterResult withCriteria(Closure criteriaClosure, params = [:]){
        result = FilterResult.newInstance()

        result.hasStatistics = false
        result.results = getCriteria().list(params, criteriaClosure)

        result
    }

    private getCriteria(){
        targetClass.createCriteria()
    }

    /**
     * Must return the class where the Filter will iterate.
     * @return
     */
    abstract def getTargetClass()

    abstract Boolean isPaginationEnabled()


}
