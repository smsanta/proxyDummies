package proxydummies

import proxydummies.utilities.Logger

class XmlNavigator {

    private Node xmlObject

    private def currentElement

    XmlNavigator( String plainXml ){
        xmlObject = XmlParser.newInstance().parseText( plainXml )
        currentElement = xmlObject
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
        currentElement = xmlObject.get("S:Header").first().get("ns4:HeaderComunRequest").first()
        this
    }

    def getBody(){
        currentElement = xmlObject.get("S:Body").first()
        this
    }

    def reset(){
        currentElement = xmlObject
        this
    }

    def value(asList = false){
        if(asList){
            currentElement.value()
        } else {
            currentElement.value().first()
        }
    }
}
