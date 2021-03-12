package proxydummies.exceptions

import proxydummies.utilities.DummiesMessageCode

class DummiesException extends Exception{

    Integer code
    String message

    DummiesException(Integer pCode, String pMessage = null, String attachToMessage = "") {
        code = pCode
        message = pMessage ?: getMessageByCode( pCode )
        message += attachToMessage
    }

    def getMap(){
        [status : code, message : message]
    }

    def static getMessageByCode(Integer code) {
        DummiesMessageCode.getMessage( code )
    }

    def static throwEx( Integer pCode ){
        throw new DummiesException(pCode)
    }

    /**
     * Returns a cute formatted stack trace.
     *
     * @param throwable
     * @return
     */
    static String getStringTrace(Throwable throwable = null){
        Writer writer = new StringWriter()
        PrintWriter printWriter = new PrintWriter(writer)

        throwable = throwable ?: this
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}
