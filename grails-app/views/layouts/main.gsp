<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

    <asset:stylesheet src="application.css"/>

    <g:layoutHead/>
</head>

<body>

<g:layoutBody/>

<div id="modals">
    <!-- Alert - Central Modal -->
    <div id="main-alert" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="main-alert-title" aria-hidden="true">

        <!-- Change class .modal-sm (sm, md, lg, fluid) to change the size of the modal -->
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="main-alert-title modal-title w-100" id="modal-title">Modal title</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="main-alert-body modal-body">
                    ...
                </div>
                <div class="main-alert-exchange body-exchange" style="display: none;">
                </div>
                <div class="main-alert-footer modal-footer">
                    <button type="button" class="btn btn-secondary btn-sm" data-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Alert - Central Modal -->

    <!-- Modal Success -->
    <div id="success-modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-notify modal-success" role="document">
            <!--Content-->
            <div class="modal-content">
                <!--Header-->
                <div class="modal-header">
                    <p class="success-modal-title heading lead">Modal Success</p>

                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true" class="white-text">&times;</span>
                    </button>
                </div>

                <!--Body-->
                <div class="modal-body">
                    <div class="text-center">
                        <i class="fas fa-check fa-4x mb-3 animated rotateIn"></i>
                        <p class="success-modal-body">Lorem ipsum dolor sit amet</p>
                    </div>
                </div>

                <!--Footer-->
                <div class="modal-footer justify-content-center">
                    <a type="button" class="btn btn-success">Get it now <i class="far fa-gem ml-1 text-white"></i></a>
                    <a type="button" class="btn btn-outline-success waves-effect" data-dismiss="modal">No, thanks</a>
                </div>
            </div>
            <!--/.Content-->
        </div>
    </div>
    <!-- Modal Success-->

    <!-- Modal Danger -->
    <div id="error-modal" class="modal fade"  tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-notify modal-danger" role="document">
            <!--Content-->
            <div class="modal-content">
                <!--Header-->
                <div class="modal-header">
                    <p class="error-modal-title heading lead">Modal Danger</p>

                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true" class="white-text">&times;</span>
                    </button>
                </div>

                <!--Body-->
                <div class="modal-body">
                    <div class="text-center">
                        <i class="fas fa-times-circle fa-4x mb-3 animated rotateIn"></i>
                        <p class="error-modal-body">Lorem ipsum dolor sit amet</p>
                    </div>
                </div>

                <!--Footer-->
                <div class="modal-footer justify-content-center">
                    <a type="button" class="btn btn-danger">Get it now <i class="far fa-gem ml-1 text-white"></i></a>
                    <a type="button" class="btn btn-outline-danger waves-effect" data-dismiss="modal">No, thanks</a>
                </div>
            </div>
            <!--/.Content-->
        </div>
    </div>
    <!-- Modal Danger -->

    <!-- Alert - Central Modal -->
    <div id="popup-alert" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="popup-alert-title" aria-hidden="true">

        <!-- Change class .modal-sm (sm, md, lg, fluid) to change the size of the modal -->
        <div class="modal-dialog modal-xl" style="height: -webkit-fill-available;" role="document">
            <div class="modal-content h-100">
                <div class="modal-header">
                    <h4 class="popup-alert-title modal-title w-100" id="popup-alert-modal-title">Modal title</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="popup-alert-body modal-body">
                    ...
                </div>
                <div class="popup-alert-exchange body-exchange" style="display: none;"></div>
                <div class="main-alert-footer modal-footer d-none">
                    <button type="button" class="btn btn-secondary btn-sm" data-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Alert - Central Modal -->
</div>

<asset:javascript src="application.js"/>

<script>
    $(document).ready(function () {
        app.initialize();
    });
</script>
</body>
</html>
