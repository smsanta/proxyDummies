package proxydummies

class StringExtension {

    static def extendStringMehtods(){

        //Attach utility events to String class.
        String.metaClass.static.toCamelCase = { boolean capitalized = false ->
            def text = delegate.replaceAll( "(_)([A-Za-z0-9])", { Object[] dis -> dis[2].toUpperCase() } )
            capitalized ? text?.capitalize() : text
        }

        String.metaClass.static.toSnakeCase = {
            delegate.trim().replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
        }
    }


}
