package proxydummies

import java.text.SimpleDateFormat

class DateExtension {

    static void extendDateMehtods(){
        Date.metaClass.static.format = { String pattern ->
            SimpleDateFormat.newInstance( pattern ).format( delegate )
        }
    }
}
