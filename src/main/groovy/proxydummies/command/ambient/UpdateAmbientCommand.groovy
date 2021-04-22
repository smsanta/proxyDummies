package proxydummies.command.ambient

class UpdateAmbientCommand extends CreateAmbientCommand {

    Long id

    static constraints = {
        id nullable: false, min: 1L
    }

}
