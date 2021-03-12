class DuumyObject{

    constructor(){ }

    static newInstance( cb = (newItem) => newItem ){
        return cb ( eval( `new ${this.name}()`)) ;
    }
}

