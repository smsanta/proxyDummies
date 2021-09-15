class Recurrent{
    constructor(callback, delay){
        this.id;
        this.started;
        this.running = false;
        this.callback = callback;
        this.delay = delay;
    }

    start() {
        let _this = this;
        _this.running = true;
        _this.started = new Date();

        this.id = this._createRecurrence( function (){
            _this.started = new Date();
            _this.callback();
        }, this.delay);

        return this;
    }

    stop(){
        this.running = false;
        this._stopRecurrence();
        return this;
    }

    trigger(){
        this.callback();
        return this;
    }

    getTimeLeft(){
        let remaining = 0;
        if ( this.running ) {
            remaining = this.delay - (new Date() - this.started)
        }

        return remaining
    }

    isRunning() {
        return this.running
    }

    _createRecurrence(callback, timer){
        throw new Error('Method not implemented!');
    }

    _stopRecurrence(){
        throw new Error('Method not implemented!');
    }
}