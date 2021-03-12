package proxydummies

import grails.util.Environment
import proxydummies.utilities.Logger

class  BootStrap {

    SystemConfigsService systemConfigsService
    FileServicesService fileServicesService

    def dataSource

    def init = { servletContext ->
        checkDb()
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
            "redirectUrl" : "http://localhost:8888",
            "proxyDummiesHome" : proxyDummiesHome,
            "saveResponses" : true,
            "saveResponsesFolder" : saveResponsesFolder
        ]

        initialConfigs.each { confKey, confValue ->
            if ( !systemConfigsService.getConfigByKey( confKey ) ){
                Logger.info( this, "Saving new Config $confKey -> $confValue" )
                systemConfigsService.createNewConfig( confKey, confValue )
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
}
