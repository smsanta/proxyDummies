package proxydummies.command.environment

import proxydummies.command.abstracts.MainCommand

class SaveEnvironmentCommand extends MainCommand{

    Long id
    String url
    String name
    String uriPrefix

    static constraints = {
        id nullable: true
        url nullable: false, blank: false
        name nullable: true, blank: true
        uriPrefix nullable: false, blank: false
    }

    @Override
    def toMapObject() {
        [
            id: id,
            uri: url,
            name: name,
            uriPrefix: uriPrefix
        ]
    }
}
