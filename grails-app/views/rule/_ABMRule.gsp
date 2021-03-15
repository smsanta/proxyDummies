<form class="rule-form">
    <input type="hidden" id="abm_input_id">
    <div class="row">
        <div class="col-1">
            <label for="abm_input_uri" class="col-form-label">Uri</label>
        </div>
        <div class="col-5">
            <input type="text" id="abm_input_uri" class="form-control" aria-describedby="uriHelpInline" placeholder="Hookup uri ej: /esb/EAI/ChequeElectronico_Buscar/v1.0">
        </div>
        <div class="col-auto d-none">
            <span id="uriHelpInline" class="form-text">
                Must be 8-20 characters long.
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-1">
            <label for="abm_input_priority" class="col-form-label">Prioridad</label>
        </div>
        <div class="col-5">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text c-pointer" data-bs-toggle="tooltip" data-bs-placement="left" title="La prioridad es en base al valor númerico mas alto."> <i class="bi-info-circle-fill" style="color: orange"></i> </span>
                </div>
                <input id="abm_input_priority" type="text" class="form-control" placeholder="1, 2, 3, etc." aria-label="Username" aria-describedby="basic-addon1">
            </div>
        </div>
        <div class="col-auto d-none">
            <span id="priorityHelpInline" class="form-text">
                Must be 8-20 characters long.
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-1">
            <label for="abm_input_type" class="col-form-label">Tipo</label>
        </div>
        <div class="col-5">
            <select id="abm_input_type" class="form-select" aria-label="Default select example">
                <g:each in="${proxydummies.Rule.SourceType.values()}" var="eachType">
                    <option value="${eachType.name()}"
                    <g:if test="${eachType.ordinal() == 0}">
                        selected
                    </g:if>

                    >${eachType.name()}</option>
                </g:each>
            </select>
        </div>
    </div>

    <div class="row">
        <div class="col-1">
            <label for="abm_input_data" class="col-form-label">Data</label>
        </div>
        <div class="col-5">
            <textarea type="text" id="abm_input_data" class="form-control" aria-describedby="dataHelpInline" placeholder="Ruta completa al archivo: C:\folder\file.xml"></textarea>
        </div>
        <div class="col-auto">
            <span id="dataHelpInline" class="form-text d-none">
                Must be 8-20 characters long.
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-1">
            <label for="abm_input_state" class="col-form-label">Estado</label>
        </div>
        <div class="col-5 form-switch">
            <input type="checkbox" id="abm_input_state" class="form-check-input" aria-describedby="stateHelpInline">
        </div>
        <div class="col-auto">
            <span id="stateHelpInline" class="form-text d-none">
                Must be 8-20 characters long.
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-1">
            <label for="abm_input_description" class="col-form-label">Descripción</label>
        </div>
        <div class="col-5">
            <input type="text" id="abm_input_description" class="form-control" aria-describedby="descriptionHelpInline">
        </div>
        <div class="col-auto">
            <span id="descriptionHelpInline" class="form-text d-none">
                Must be 8-20 characters long.
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-6" style="text-align: center;">
            <button id="btn-save-rule" type="submit" class="btn btn-primary">Guardar</button>
        </div>
    </div>

</form>