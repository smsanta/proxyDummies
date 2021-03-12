package proxydummies.exceptions

class ApiException extends DummiesException {

    Map result

    ApiException(Integer pCode, String pMessage, Map pResult = [:]) {
        super(pCode, pMessage)
        result = pResult
    }

    ApiException(Integer pCode, Map pResult){
        super(pCode)
        result = pResult
    }
}