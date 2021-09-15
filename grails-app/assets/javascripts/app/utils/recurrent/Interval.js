class Interval extends Recurrent{

    constructor( callback, timer ){
        super(callback, timer);
    }
    _createRecurrence(callback, timer) {
        return setInterval(callback, timer)
    }

    _stopRecurrence() {
        clearInterval( this.id );
    }

}