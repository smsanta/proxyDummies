package proxydummies.command.rule

class UpdateRuleCommand extends CreateRuleCommand {

    Long id

    static constraints = {
        id nullable: false, min: 1L
    }

}
