package proxydummies.command

import proxydummies.command.abstracts.MainCommand

class IdCommand extends MainCommand {

    Long id

    static constraints = {
        id nullable: false, min: 1L
    }

    @Override
    def toMapObject() {
        [id: id]
    }
}
