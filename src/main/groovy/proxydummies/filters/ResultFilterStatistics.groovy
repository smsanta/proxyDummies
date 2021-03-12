package proxydummies.filters

import proxydummies.abstracts.AbstractObject


class ResultFilterStatistics implements AbstractObject {

    Integer page
    Integer pageSize
    Integer offset
    Integer lastPage
    Integer totalResults

    @Override
    def toMapObject() {
        [ page: page, pageSize: pageSize, offset: offset, lastPage: lastPage, totalResults: totalResults ]
    }

    @Override
    String toString() {
        toJsonObject()
    }
}
