package proxydummies

import grails.converters.JSON
import proxydummies.abstracts.RequestObjectNavigator

class RestNavigator extends RequestObjectNavigator{

    RestNavigator(String plainJson ){
        requestObject = JSON.parse( plainJson )
    }

    @Override
    def get(String node) {
        requestObject?."$node"
    }

    @Override
    def get(List<String> nestedJoin) {
        def element
        nestedJoin.each { String attribute ->
            element = requestObject.get( attribute )
        }

        element
    }

    @Override
    void reset() {
        //Nothing to do here atm.
    }
}
