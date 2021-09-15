class Timeout extends Recurrent{

    _createRecurrence(callback, timer) {
        return setTimeout(callback, timer);
    }

    _stopRecurrence() {
        clearTimeout( this.id );
    }

}