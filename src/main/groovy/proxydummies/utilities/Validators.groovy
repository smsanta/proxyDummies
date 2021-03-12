package proxydummies.utilities

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class Validators {

    def static validateDate(def validDate) {
        if(validDate){
            def test = validDate =~ /\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}]\:\d{2}\.\d{3}/
            if(test){
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                df.setLenient(false)
                try {
                    Date dateParam = df.parse(validDate)
                    return true
                } catch (ParseException pe) {
                    return "date.format.invalid"
                }
            }
        }
        return false
    }
}
