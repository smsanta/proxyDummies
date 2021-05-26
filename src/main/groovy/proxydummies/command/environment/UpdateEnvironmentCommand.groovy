package proxydummies.command.environment

class UpdateEnvironmentCommand extends CreateEnvironmentCommand {

    Long id

    static constraints = {
        id nullable: false, min: 1L
    }

}
