package proxydummies

import proxydummies.abstracts.AbstractController

class SetupController extends AbstractController{

    SystemConfigsService systemConfigsService

    def index() {
        def saveResponses = systemConfigsService.getSaveResponse()

        render( view: "/index", model: [saveResponses: saveResponses])
    }


}
