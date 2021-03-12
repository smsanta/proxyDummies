<form class="rule-search-form">
    <div class="d-flex flex-row justify-content-center">
        <div class="col-3">
            <div class="row g-2 align-items-center">
                <div class="col-auto">
                    <label for="inputUri" class="col-form-label" style="font-weight: bold;">Uri</label>
                </div>
                <div class="col-auto">
                    <input type="text" id="inputUri" class="form-control">
                </div>
            </div>
        </div>
        <div class="col-3">
            <div class="row g-2 align-items-center">
                <div class="col-auto">
                <label style="font-weight: bold;">Estado: </label><label for="inputActivo" class="col-form-label">Activo</label>
                </div>
                <div class="col-auto form-check">
                    <input type="checkbox" id="inputActivo" class="form-check-input" checked="checked">
                </div>
                <div class="col-auto">
                    <label for="inputInactivo" class="col-form-label">Inactivo</label>
                </div>
                <div class="col-auto form-check">
                    <input type="checkbox" id="inputInactivo" class="form-check-input" checked="checked">
                </div>
            </div>
        </div>
        <div class="col-3">
            <button id="btn-search" type="submit" class="btn btn-primary">Filtrar</button>
        </div>
    </div>
</form>