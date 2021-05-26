<form class="environment-form">
    <h1> Save Environment </h1>
    <g:each in="${proxydummies.Environment.findAll()}" var="eachEnvironment">
        <div class="row ${((eachEnvironment.id % 2) == 0) ? "even" : "odd"}" data-id="${eachEnvironment.id}">
            <div class="col-3 environment-form-data">
                <div class="w-25 environment-form-data-label">
                    <label for="abm_input_environment_name_${eachEnvironment.id}" class="col-form-label">Name</label>
                </div>
                <div class="w-75 environment-form-data-input">
                    <input type="text" id="abm_input_environment_name_${eachEnvironment.id}" class="form-control" aria-describedby="name${eachEnvironment.id}HelpInline" placeholder="Nombre descriptivo" value="${eachEnvironment.name}">
                </div>
                <div class="col-auto d-none">
                    <span id="name${eachEnvironment.id}HelpInline" class="form-text">
                        Must be 8-20 characters long.
                    </span>
                </div>
            </div>

            <div class="col-3 environment-form-data">
                <div class="w-25 environment-form-data-label">
                    <label for="abm_input_environment_url_prefix_${eachEnvironment.id}" class="col-form-label">ID</label>
                </div>
                <div class="w-75 environment-form-data-input">
                    <input type="text" id="abm_input_environment_url_prefix_${eachEnvironment.id}" class="form-control" aria-describedby="urlPrefix${eachEnvironment.id}HelpInline" placeholder="ID" value="${eachEnvironment.uriPrefix}">
                </div>
                <div class="col-auto d-none">
                    <span id="urlPrefix${eachEnvironment.id}HelpInline" class="form-text">
                        Must be 8-20 characters long.
                    </span>
                </div>
            </div>

            <div class="col-3 environment-form-data">
                <div class="w-25 environment-form-data-label">
                    <label for="abm_input_environment_redirect_url_${eachEnvironment.id}" class="col-form-label">Redirect URL</label>
                </div>
                <div class="w-75 environment-form-data-input">
                    <input type="text" id="abm_input_environment_redirect_url_${eachEnvironment.id}" class="form-control" aria-describedby="redirectUrl${eachEnvironment.id}HelpInline" placeholder="Url a donde redirigir las requests" value="${eachEnvironment.url}">
                </div>
                <div class="col-auto d-none">
                    <span id="redirectUrl${eachEnvironment.id}HelpInline" class="form-text">
                        Must be 8-20 characters long.
                    </span>
                </div>
            </div>

            <div class="col-3">
                <button id="btn-save-environment-${eachEnvironment.id}" type="submit" class="btn btn-primary update-environment-btn" action="save">Guardar</button>
                <i id="delete-${eachEnvironment.id}" class="bi-trash-fill action-icon" style="color: red; margin-left: 30px;" action="delete" data-bs-toggle="tooltip" data-bs-placement="top" data-original-title="Eliminar Environment"></i>
            </div>
        </div>
    </g:each>
</form>
<div class="row">
    <div class="col-12 add-environment">
        <button id="btn-add-environment" type="submit" class="btn btn-primary add-environment-btn" action="add-new">Agregar Environment</button>
    </div>
</div>