<form class="rule-form">
    <h1> Save Rule </h1>
    <input type="hidden" id="abm_input_id">
    <div class="row">
        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_uri" class="col-form-label">Uri</label>
            </div>
            <div class="w-75 rule-form-data-input">
                <input type="text" id="abm_input_uri" class="form-control" aria-describedby="uriHelpInline" placeholder="Hookup uri ej: /esb/EAI/ChequeElectronico_Buscar/v1.0">
            </div>
            <div class="col-auto d-none">
                <span id="uriHelpInline" class="form-text">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>

        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_priority" class="col-form-label">Prioridad</label>
            </div>
            <div class="w-75 rule-form-data-input">
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
    </div>

    <div class="row">
        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_method" class="col-form-label">Request Method</label>
            </div>
            <div class="w-75 rule-form-data-input">
                <select id="abm_input_method" class="form-select" aria-label="Default select example">
                    <g:each in="${proxydummies.Rule.HttpMethod.values()}" var="eachTMethod">
                        <option value="${eachTMethod.name()}"
                            <g:if test="${eachTMethod.ordinal() == 0}">
                                selected
                            </g:if>

                        >${eachTMethod.name()}</option>
                    </g:each>
                </select>
            </div>
        </div>

        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_service_type" class="col-form-label">Tipo de Servicio</label>
            </div>
            <div class="w-75 rule-form-data-input">
                <div class="w-25 float-left">
                    <select id="abm_input_service_type" class="form-select" aria-label="Default select example">
                        <g:each in="${proxydummies.Rule.ServiceType.values()}" var="eachServiceType">
                            <option value="${eachServiceType.name()}"
                                <g:if test="${eachServiceType.ordinal() == 0}">
                                    selected
                                </g:if>

                            >${eachServiceType.name()}</option>
                        </g:each>
                    </select>
                </div>
                <div class="form-switch w-75 float-left">
                    <i class="bi-info-circle-fill" data-bs-toggle="tooltip" data-bs-placement="left" title="Si permanece activado se agregará a los Response Headers un header extra por default pre-defeinido según el Tipo de Servicio.
                        Rest: ['Content-Type': 'application/json']
                        SOAP: ['Content-Type': 'text/xml']"
                    style="color: orange"></i>
                    <label for="abm_input_default_header" class="col-form-label">Incluir default Content-Type al header?</label>
                    <input type="checkbox" checked="checked" id="abm_input_default_header" class="form-check-input" style="margin-left: 10px;">
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_response_status" class="col-form-label">Response Status</label>
            </div>
            <div class="w-75 rule-form-data-input">
                <input type="text" id="abm_input_response_status" class="form-control" aria-describedby="responseStatusHelpInline" value="200">
            </div>
            <div class="col-auto">
                <span id="responseStatusHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>

        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_state" class="col-form-label">Estado</label>
            </div>
            <div class="w-75 rule-form-data-input form-switch">
                <input type="checkbox" id="abm_input_state" class="form-check-input" aria-describedby="stateHelpInline">
            </div>
            <div class="col-auto">
                <span id="stateHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_type" class="col-form-label">Tipo</label>
            </div>
            <div class="w-75 rule-form-data-input">
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

        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_description" class="col-form-label">Descripción</label>
            </div>
            <div class="w-75 rule-form-data-input">
                <input type="text" id="abm_input_description" class="form-control" aria-describedby="descriptionHelpInline">
            </div>
            <div class="col-auto">
                <span id="descriptionHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_data" class="col-form-label">Data</label>
            </div>
            <div class="w-75 rule-form-data-input">
                <textarea style="height: 180px;" type="text" id="abm_input_data" class="form-control" aria-describedby="dataHelpInline" placeholder="
                Tipo FILE: Ruta completa al archivo: C:\folder\file.xml|json
                Tipo DATABASE: Ingresar todo el contenido plano.
                "></textarea>
            </div>
            <div class="col-auto">
                <span id="dataHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>

        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_respone_headers" class="col-form-label">
                    Response Headers
                    <i class="bi-info-circle-fill" data-bs-toggle="tooltip" data-bs-placement="left" title="Expresión Groovy/Java, debe retornar un Map." style="color: orange"></i>
                </label>
            </div>
            <div class="w-75 rule-form-data-input">
                <textarea style="height: 180px;" type="text" id="abm_input_respone_headers" class="form-control" aria-describedby="responsetHeadersHelpInline" placeholder="Expresión que debe retornar un map con los headers a ser incluidos
                Ej: ['Content-Type': 'application/json'] "></textarea>
            </div>
            <div class="col-auto">
                <span id="responseHeadersHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_request_condition_active" class="col-form-label">Activar condición sobre la Request</label>
            </div>
            <div class="w-75 rule-form-data-input form-switch">
                <input type="checkbox" id="abm_input_request_condition_active" class="form-check-input" aria-describedby="requestConditionActiveHelpInline">
            </div>
            <div class="col-auto">
                <span id="requestConditionActiveHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>

        <div class="col-5 rule-form-data">
            <div class="w-25 rule-form-data-label">
                <label for="abm_input_request_condition" class="col-form-label">
                    Request Condition Expression

                    <i class="bi-info-circle-fill text-blue" data-bs-toggle="tooltip" data-bs-placement="left" title="
                    La expression debe retornar un Boolean.
                    Para mas información sobre como generar expressiones vea: H1 y H2"></i>
                    <i class="bi-arrow-right"></i>
                    <a href="https://docs.groovy-lang.org/latest/html/api/groovy/util/XmlParser.html" target="_blank">
                        <i class="bi-type-h1 text-blue" class="c-pointer" data-bs-toggle="tooltip" data-bs-placement="left" title="Click para ver sobre XmlParser"></i>
                    </a>
                    <a href="https://github.com/smsanta/proxyDummies/tree/master/grails-app/utils/proxydummies/XmlNavigator.groovy" target="_blank">
                        <i class="bi-type-h2 text-blue" class="c-pointer" data-bs-toggle="tooltip" data-bs-placement="left" title="Click para ver la clase wrapper."></i>
                    </a>
                    <br>
                </label>
            </div>
            <div class="w-75 rule-form-data-input">
                <textarea style="height: 180px;" type="text" id="abm_input_request_condition" class="form-control" aria-describedby="requestConditionHelpInline" placeholder="
            Expression a ser evaluada sobre los parametros de la request.
            Variable de la Request $request = [ payload: [:], params: [:] ]
            Ej: POST - REST - Payload-> { status: 200, result: { 'color' : 'green' } }
                CONDITION:
                $request.payload.get('status') == '200'
                && $request.payload.get( ['result', 'color'] == 'green' )"
                ></textarea>
            </div>
            <div class="col-auto">
                <span id="requestCondtionHelpInline" class="form-text d-none">
                    Must be 8-20 characters long.
                </span>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-7" style="text-align: center;">
            <button id="btn-save-rule" type="submit" class="btn btn-primary">Guardar</button>
        </div>
    </div>

</form>