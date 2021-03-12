<!doctype html>
<html>
    <head>
        <title><g:if env="development">Grails Runtime Exception</g:if><g:else>Error</g:else></title>
        <meta name="layout" content="main">
        <g:if env="development"><asset:stylesheet src="errors.css"/></g:if>
    </head>
    <body>
        <ul class="errors">
            <li> Error al conectar a la db ejecutar el siguiente script. </li>
        </ul>
        <p> ${proxydummies.StaticScripts.CREATE_DB_USER} </p>
    </body>
</html>
