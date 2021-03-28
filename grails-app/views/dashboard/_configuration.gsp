<div class="configuration-cards row">

    <g:each in="${proxydummies.Configuration.findAll()}" var="eachConfig">
        <div class="card border border-primary text-dark bg-light mb-3 align-items-center" style="max-width: 30%;">
            <div class="card-header">
                <i class="bi-info-circle-fill text-blue" data-bs-toggle="tooltip" data-bs-placement="left" title="" data-original-title="${eachConfig.description}" style="float: right;"></i>
                <h5> ${eachConfig.title} </h5>
            </div>
            <div class="card-body">
                <textarea id="ta-config-${eachConfig.key}">${eachConfig.value}</textarea>
            </div>
            <div class="card-footer">
                <button class="btn btn-primary btn-update-config" data-key="${eachConfig.key}"> Guardar </button>
            </div>
        </div>
    </g:each>



</div>
