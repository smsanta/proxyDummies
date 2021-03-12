<div class="configuration-cards row">

    <g:each in="${proxydummies.Configuration.findAll()}" var="eachConfig">
        <div class="card border border-primary text-dark bg-light mb-3 align-items-center" style="max-width: 18rem;">
            <div class="card-header"> <h5> ${eachConfig.key} </h5> </div>
            <div class="card-body">
                <textarea id="ta-config-${eachConfig.key}">${eachConfig.value}</textarea>
            </div>
            <div class="card-footer">
                <button class="btn btn-primary btn-update-config" data-key="${eachConfig.key}"> Guardar </button>
            </div>
        </div>
    </g:each>



</div>
