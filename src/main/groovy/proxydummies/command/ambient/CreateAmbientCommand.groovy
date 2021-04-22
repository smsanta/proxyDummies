package proxydummies.command.ambient

import proxydummies.command.abstracts.MainCommand

class CreateAmbientCommand extends MainCommand{

    String url
    String name

    static constraints = {
        url nullable: false, blank: false
        name nullable: true, blank: true
    }

    @Override
    def toMapObject() {
        [
            uri: url,
            name: name
        ]
    }
}
