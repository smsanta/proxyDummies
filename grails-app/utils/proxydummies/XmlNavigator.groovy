package proxydummies

import proxydummies.abstracts.RequestObjectNavigator
import proxydummies.utilities.Logger

class XmlNavigator extends RequestObjectNavigator {

    private Node currentElement

    XmlNavigator( String plainXml ){
        requestObject = XmlParser.newInstance().parseText( plainXml )
        currentElement = requestObject
    }

    def get( String node, asList = false){
        Logger.info(this, "Joining Node: $node.")
        if( asList ){
            currentElement = currentElement.get( node )
        } else {
            currentElement = currentElement.get( node ).first()
        }

        this
    }

    def get(List<String> nestedNodes, asList = false){
        String lastElement = nestedNodes.last()
        nestedNodes.remove(lastElement)

        Logger.info(this, "Joining List Node: $nestedNodes.")
        nestedNodes.each {
            Logger.info(this, "Iterating Over node: $it")
            currentElement = currentElement.get( it ).first()
        }

        if( asList ){
            currentElement = currentElement.get( lastElement )
        } else {
            currentElement = currentElement.get( lastElement ).first()
        }

        this
    }

    def getHeaderComunRequest(){
        currentElement = requestObject.get("S:Header").first().get("ns4:HeaderComunRequest").first()
        this
    }

    def getBody(){
        currentElement = requestObject.get("S:Body").first()
        this
    }

    void reset(){
        currentElement = requestObject
    }

    def value(asList = false){
        if(asList){
            currentElement.value()
        } else {
            currentElement.value().first()
        }
    }
}
