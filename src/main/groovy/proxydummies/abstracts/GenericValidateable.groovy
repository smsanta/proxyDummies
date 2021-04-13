package proxydummies.abstracts

import grails.validation.Validateable
import org.springframework.validation.FieldError

import java.text.MessageFormat

trait GenericValidateable implements Validateable{

    def getErrorMap(){
        def errorMap = [:]
        getErrors().getAllErrors().each { FieldError error ->
            def errorStack = errorMap[(error.field)] ?: []

            errorStack << MessageFormat.format( error.defaultMessage, [error.field].toArray())

            errorMap[(error.field)] = errorStack
        }
        errorMap
    }

    String getPlainErrors(){
        String errorStack = ""
        getErrorMap().each { def field, List listError ->
            listError.each {
                errorStack += it + "\n "
            }
        }
        errorStack
    }

}