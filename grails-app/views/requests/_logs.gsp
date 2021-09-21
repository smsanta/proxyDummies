<table id="table-request-logs" class="table">
    <thead class="table-success">
        <tr>
            <th class="t-align-center" scope="col"> Fecha </th>
            <th class="t-align-center" scope="col">Status</th>
            <th class="t-align-center" scope="col">Method</th>
            <th class="t-align-center col-3" scope="col" style="max-width: 600px;">Uri</th>
            <th class="t-align-center" scope="col">Request</th>
            <th class="t-align-center" scope="col">Response</th>
            <th class="t-align-right logs-panel-table-header-buttons" scope="col">
                <i data-active="true" data-bs-toggle="tooltip" data-bs-placement="top" title="" id="request-log-autoupdate" class="bi-stop-circle text-red action-icon" data-original-title="Actualizacion en tiempo real ON/OFF"></i>
                <i data-bs-toggle="tooltip" data-bs-placement="top" title="" id="request-log-hide" class="bi-eye-slash text-red action-icon" data-original-title="Ocultar"></i>
                <i data-bs-toggle="tooltip" data-bs-placement="top" title="" id="request-log-loader" class="bi-reception-0 text-red action-icon" data-original-title="Siguiente Actualizacion"></i>
                <select id="request-log-autoupdate-results" class="form-select" aria-label="Cantidad de Resultados" style="width: 60px; max-width: 60px;">
                    <option value="1000" selected>*</option>
                    <option value="5">5</option>
                    <option value="10">10</option>
                    <option value="20">20</option>
                </select>
            </th>
        </tr>
    </thead>
    <tbody>

    </tbody>
</table>