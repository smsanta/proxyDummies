package proxydummies.command.configuration

class UpdateConfigurationCommand extends ConfigurationKeyCommand{

    String value

    static constraints = {
        value nullable: false, blank: false
    }

    @Override
    def toMapObject() {
        [ key: key, value: value ]
    }
}
