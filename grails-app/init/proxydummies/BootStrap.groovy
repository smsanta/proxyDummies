package proxydummies

import grails.util.Environment
import proxydummies.utilities.Logger

import java.text.SimpleDateFormat

class  BootStrap {

    SystemConfigsService systemConfigsService
    FileServicesService fileServicesService

    def dataSource

    def init = { servletContext ->
        checkDb()
        extendFuntionalities()
        loadInitialConfig()
        loadTestData()
    }

    def destroy = {
    }

    void loadInitialConfig() {
        Logger.info(this, "Checking initial configs.")

        def proxyDummiesHome = fileServicesService.buildDefaultProxyDummiesHomeFolder()
        def saveResponsesFolder = fileServicesService.buildDefaultProxyDummiesSaveResponseFolder()

        def initialConfigs = [
            [
                key: "redirectUrl",
                value: "http://localhost:8888",
                title: "Url de redirección",
                description: "Se utiliza para redirigir todas request hacia esta url manteniendo la misma URI."
            ],
            [
                key: "proxyDummiesHome",
                value: proxyDummiesHome,
                title: "Carpeta Principal",
                description: "Carpeta destinada a guardar archivos del proxy dummies."
            ],
            [
                key: "saveResponses",
                value: true,
                title: "Guardar Respuestas?",
                description: "Variable que determina si guardar los responses de las request. Valores posibles: \"true\" o \"false\""
            ],
            [
                key: "saveResponsesFolder",
                value: saveResponsesFolder,
                title: "Carpeta de Responses",
                description: "Carpeta destinada a guardar los responses de las requests."
            ],
            [
                key: "overrideSaveResponses",
                value: false,
                title: "Sobreescribir Respuestas?",
                description: "Variable que determina si al guardar un response debe sobreescribir el mismo archivo o generar nuevos por cada request. Valores posibles: \"true\" o \"false\""
            ],
            [
                key: "overrideSaveResponsesExpression",
                value: '"__" + Date.newInstance().format("yyyy-MM-dd_HH.mm.ss.S") + "__"',
                title: "Expresion Nombre de Archivo",
                description: 'Valor Anexo al nombre del archivo para que no se repita. Aplica la mascara: "DUMMIES." + expression +  "URI PATH" + ".mxl"'
            ],
        ]

        initialConfigs.each { conf ->
            Configuration config = systemConfigsService.getConfigByKey( conf.key )
            if ( config ){
                Logger.info( this, "Updating Config $config.key." )
                systemConfigsService.updateConfig( config.key, null, conf.description, conf.title )
            }else {
                Logger.info( this, "Saving new Config $conf.key." )
                systemConfigsService.createNewConfig( conf.key, conf.value, conf.description, conf.title )
            }
        }
    }

    void loadTestData() {
        if( !Environment.current.is( Environment.PRODUCTION ) && false){
            Rule testRule = Rule.newInstance()
            testRule.uri = "/esb/EAI/ChequeElectronico_Buscar/v1.0"
            testRule.data = "C:\\work\\environment\\DummiesFastrack\\DUMMIES.esb.EAI.ChequeElectronico_Buscar.v1.0.xml"
            testRule.sourceType = Rule.SourceType.FILE
            testRule.active = false
            testRule.priority = 1

            testRule.save( flush: true )
        }

    }

    void checkDb() {
        try {
            dataSource.connection.isValid( 1000 )
        } catch (Exception e) {
            Logger.info(this, "La aplicación no pudo ejecutarse por que no se pudo conectar a la db.")
            Logger.info(this, "Ejecute el siguiente script y intente nuevamente.")
            Logger.info(this, StaticScripts.CREATE_DB_USER)
            throw e;
        }
    }

    void extendFuntionalities(){
        StringExtension.extendStringMehtods()
        DateExtension.extendDateMehtods()
    }
}
