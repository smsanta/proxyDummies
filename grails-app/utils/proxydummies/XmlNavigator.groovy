package proxydummies

class XmlNavigator {

    private Node xmlObject

    private def currentElement

    XmlNavigator( String plainXml ){
        xmlObject = XmlParser.newInstance().parseText( plainXml )
        currentElement = xmlObject
    }

    def get( String node, asList = false){
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

        nestedNodes.each {
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
        currentElement = xmlObject.get("S:body").first()
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
