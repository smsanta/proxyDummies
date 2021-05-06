package proxydummies

import grails.util.Environment
import io.micronaut.http.HttpMethod
import proxydummies.utilities.Logger

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
                key: "globalRedirectUrl",
                value: "http://localhost:8888",
                title: "Url de redirección global",
                description: "Se utiliza para redirigir todas request hacia esta url manteniendo la misma URI."
            ],
            [
                key: "enableGlobalRedirectUrl",
                value: true,
                title: "Habilita la Url de redirección global",
                description: "Habilita el redireccionamiento global de URLs(Cuando no se encuentra ninguna Rule que matchee.)."
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
            [
                key: "autoGenerateImportDummyFromResponses",
                value: true,
                title: "Auto-Generar archivo de import?",
                description: "Variable que determina si al guardar un response debe generar un file con el snippet para imporatar esa request como dummy. Valores posibles: \"true\" o \"false\""
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
        if( !Environment.current.is( Environment.PRODUCTION ) ){
            proxydummies.Environment environmentMembrane = new proxydummies.Environment()
            environmentMembrane.url = "http://localhost:8888"
            environmentMembrane.serviceType = proxydummies.Environment.ServiceType.SOAP
            environmentMembrane.name = "Bancon Membrane Loopback"
            environmentMembrane.save( flush: true, failOnError: true)

            proxydummies.Environment environmentFrontendRest = new proxydummies.Environment()
            environmentFrontendRest.url = "http://localhost:8088"
            environmentFrontendRest.serviceType = proxydummies.Environment.ServiceType.REST
            environmentFrontendRest.name = "Bancon Fronend"
            environmentFrontendRest.save( flush: true, failOnError: true)

            Rule testRule = Rule.newInstance()
            testRule.uri = "/esb/EAI/ChequeElectronico_Buscar/v1.0"
            testRule.data = "C:\\Users\\u900574\\proxyDummies\\responses\\DUMMIES__2021-04-13_08.11.04.717__.esb.EAI.ChequeElectronico_Buscar.v1.0.xml"
            testRule.sourceType = Rule.SourceType.FILE
            testRule.active = true
            testRule.priority = 1
            testRule.environment = environmentMembrane
            testRule.method = HttpMethod.POST
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
