package proxydummies.command.environment

import proxydummies.command.abstracts.MainCommand

class CreateEnvironmentCommand extends MainCommand{

    String url
    String name
    String uriPrefix

    static constraints = {
        url nullable: false, blank: false
        name nullable: true, blank: true
        uriPrefix nullable: false, blank: false
    }

    @Override
    def toMapObject() {
        [
            uri: url,
            name: name,
            uriPrefix: uriPrefix
        ]
    }
}
