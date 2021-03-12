<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>ProxyDummies</title>
</head>
<body>
    <div id="content" role="main">
        <nav class="navbar navbar-expand-lg navbar-dark navbar-static-top" role="navigation">
            <a class="navbar-brand" href=""><asset:image src="pmlogo.png" alt="Proxy Manager Logo" id="logo"/></a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" aria-expanded="false" style="height: 0.8px;" id="navbarContent">
                <ul class="nav navbar-nav ml-auto">
                    <li class="nav-switcher">
                        <form class="form-switch">
                            <label class="form-check-label" for="flexSwitchCheckDefault">Save Responses</label>
                            <input class="form-check-input" type="checkbox" id="flexSwitchCheckDefault" ${saveResponses ? "checked" : ""}>
                        </form>
                    </li>
                </ul>
            </div>
        </nav>


        <div id="main-content" class="row-fluid col-12 h-100">
            <div class="d-flex align-items-start h-100">
                <div class="nav flex-column nav-pills" id="v-pills-tab" role="tablist" aria-orientation="vertical">
                    <button class="nav-link active" id="v-pills-home-tab" data-bs-toggle="pill" data-bs-target="#v-pills-home" type="button" role="tab" aria-controls="v-pills-home" aria-selected="true">
                        Dashboard
                        <span id="loader-dashboard" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="display: none;"></span>
                    </button>
                    <button class="nav-link" id="v-pills-save-rule-tab" data-bs-toggle="pill" data-bs-target="#v-pills-profile" type="button" role="tab" aria-controls="v-pills-profile" aria-selected="false">
                        Save Rule
                        <span id="loader-save-rule" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="display: none;"></span>
                    </button>
                    <button class="nav-link" id="v-pills-settings-tab" data-bs-toggle="pill" data-bs-target="#v-pills-settings" type="button" role="tab" aria-controls="v-pills-settings" aria-selected="false">
                        Configuración
                        <span id="loader-configuration" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="display: none;"></span>
                    </button>
                </div>
                <div class="tab-content w-100 h-100" id="v-pills-tabContent">
                    <div class="tab-pane fade w-100 h-100 active show" id="v-pills-home" role="tabpanel" aria-labelledby="v-pills-home-tab">
                        <g:render template="/dashboard/searchRuleForm" />

                        <div class="main-table">
                            <g:render template="/dashboard/mainTable" />
                        </div>
                    </div>

                    <div class="tab-pane fade w-100 h-100" id="v-pills-profile" role="tabpanel" aria-labelledby="v-pills-profile-tab">
                        <g:render template="/rule/ABMRule" model=""/>
                    </div>

                    <div class="tab-pane fade w-100 h-100" id="v-pills-settings" role="tabpanel" aria-labelledby="v-pills-settings-tab">
                        <g:render template="/dashboard/configuration" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
