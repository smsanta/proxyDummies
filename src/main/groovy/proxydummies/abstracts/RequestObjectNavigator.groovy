package proxydummies.abstracts

abstract class RequestObjectNavigator {

    protected def requestObject

    abstract def get( String node )
    abstract def get( List<String> nestedNodes )

    abstract void reset()

}
