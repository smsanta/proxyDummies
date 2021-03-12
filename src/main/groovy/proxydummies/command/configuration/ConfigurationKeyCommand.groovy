package proxydummies.command.configuration

import proxydummies.SystemConfigsService
import proxydummies.command.abstracts.MainCommand

class ConfigurationKeyCommand extends MainCommand {

    String key

    static constraints = {
        key nullable: false, blank: false, inList: SystemConfigsService.getAllConfigurationKeys()
    }

    @Override
    def toMapObject() {
        [ key: key ]
    }
}
