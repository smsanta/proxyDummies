package proxydummies.command.abstracts

import proxydummies.abstracts.AbstractObject
import proxydummies.abstracts.GenericValidateable
import proxydummies.abstracts.Populable


abstract class MainCommand implements GenericValidateable, AbstractObject, Populable {

    @Override
    String toString() {
        toMapObject()
    }
}
