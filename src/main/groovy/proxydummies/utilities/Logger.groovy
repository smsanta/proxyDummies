package proxydummies.utilities

import org.apache.commons.logging.LogFactory

import java.security.SecureRandom

class Logger {

    static error = { parent, text ->
        def log = LogFactory.getLog( parent.class.getName() )
        log.error text;
    }

    static info = { parent, text ->
        def log =LogFactory.getLog( parent.class.getName() )
        log.info text;
    }

}
