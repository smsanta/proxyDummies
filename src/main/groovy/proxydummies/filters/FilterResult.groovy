package proxydummies.filters

import proxydummies.abstracts.AbstractObject

class FilterResult implements AbstractObject {

    List results
    ResultFilterStatistics statistics
    Boolean hasStatistics

    @Override
    def toMapObject() {
        [ items: results.collect{ it.toMapObject() } ] + ( hasStatistics ? [statistics: statistics.toMapObject()] : [:] )
    }

    def toMapObject(List includingKeys, applyToResults, Closure collector = null) {
        Closure defaultCollector = { it.toMapObject(includingKeys) }

        if( applyToResults ){
            Closure chosenCollector = (collector ? collector : defaultCollector)
            [ items: results.collect(chosenCollector)  ] + ( hasStatistics ? [statistics: statistics.toMapObject()] : [:] )
        } else {
            toMapObject( includingKeys )
        }
    }


}
