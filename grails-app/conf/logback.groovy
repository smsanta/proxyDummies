import grails.util.BuildSettings
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.StandardCharsets

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = StandardCharsets.UTF_8

        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%level] [%logger] - %msg%n"
    }
}

def targetDir = BuildSettings.TARGET_DIR

appender("FULL_STACKTRACE", FileAppender) {
    file = "${targetDir}/stacktrace.log"
    append = true
    encoder(PatternLayoutEncoder) {
        charset = StandardCharsets.UTF_8
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%level] [%logger] - %msg%n"
    }
}
logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
logger("StackTrace", INFO, ['FULL_STACKTRACE'], false)


def logAppenders = ['FULL_STACKTRACE', 'STDOUT']

root(ERROR, logAppenders)
root(INFO,  logAppenders)
