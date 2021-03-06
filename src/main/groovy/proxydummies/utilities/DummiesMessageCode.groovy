package proxydummies.utilities

class DummiesMessageCode {

    static def getMessage(Integer code){
        Messages[code]
    }

    //1000-2000 General errors
    static def GENERIC_ERROR = 1001
    static def GENERIC_MISSING_PARAMETERS = 1002
    static def DUMMY_COULD_NOT_BE_LOADED = 1003
    static def CONFIG_DOES_NOT_EXISTS = 1004
    static def GENERIC_COMMAND_VALIDATION_REJECTION = 1005
    static def SAVE_DUMMY_ALREADY_EXISTS = 1006
    static def RULE_COULD_NOT_BE_FOUND = 1007
    static def FILE_NOT_FOUND = 1008
    static def RULE_NOT_MATCHING_ANY = 1009
    static def ENVIRONMENT_COULD_NOT_BE_FOUND = 1010
    static def ENVIRONMENT_CANT_DELETE_WITH_ASOCIATED_RULE = 1011
    static def ENVIRONMENT_CANT_DELETE_DEFAULT = 1012
    static def RULE_ENVIRONMENT_REQUIRED = 1013
    static def RULE_INVALID_SERVICE_TYPE = 1014
    static def PROXY_NOWHERE_TO_REDIRECT = 1015

    static def Messages = [
        //1000-2000 General errors
        (this.GENERIC_ERROR) : "Ha ocurrido un error inesperado.",
        (this.GENERIC_MISSING_PARAMETERS) : "Parametros insuficientes para realizar la operación.",
        (this.DUMMY_COULD_NOT_BE_LOADED) : "No se pudo cargar el dummy.",
        (this.CONFIG_DOES_NOT_EXISTS) : "La configuración que intenta modificar no existe.",
        (this.GENERIC_COMMAND_VALIDATION_REJECTION) : "La validación de los parametros de entrada no ha sido superada.",
        (this.SAVE_DUMMY_ALREADY_EXISTS) : "Ya existe un dummy con la misma uri y la misma prioridad.",
        (this.RULE_COULD_NOT_BE_FOUND) : "El id de la rule no esta en la base de datos.",
        (this.FILE_NOT_FOUND) : "El archivo indicado no existe.",
        (this.RULE_NOT_MATCHING_ANY) : "No se pudo evaluar ningúna regla válida.",
        (this.ENVIRONMENT_COULD_NOT_BE_FOUND) : "El id del ENVIRONMENT no esta en la base de datos.",
        (this.ENVIRONMENT_CANT_DELETE_WITH_ASOCIATED_RULE) : "No se puede eliminar un ENVIRONMENTe que este asociado a una Rule",
        (this.ENVIRONMENT_CANT_DELETE_DEFAULT) : "No se puede eliminar un Environment que esté seleccionado como default.",
        (this.RULE_ENVIRONMENT_REQUIRED) : "El Environment no puede ser nulo.",
        (this.RULE_INVALID_SERVICE_TYPE) : "El tipo de servicio no es válido.",
        (this.PROXY_NOWHERE_TO_REDIRECT) : "No hay a donde redirigir, verifique las Rules o Active la redireccion global.",
    ]
}
