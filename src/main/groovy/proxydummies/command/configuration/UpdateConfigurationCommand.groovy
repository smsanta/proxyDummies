package proxydummies.command.configuration

class UpdateConfigurationCommand extends ConfigurationKeyCommand{

    String value
    String description
    String title

    static constraints = {
        value nullable: false, blank: false
        description nullable: true
        title nullable: true
    }

    @Override
    def toMapObject() {
        [ key: key, value: value, description: description, title: title ]
    }
}
