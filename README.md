**Bienvenido a Proxy Dummies**

Esta es una tool de desarrollo para intervenir requests y forzar respuestas que nosotros mismos deseemos.
Utiliza un metodo generico de redirección cuando el proxy dummies recibe una request en **/proxyDummies** es donde inicia
el flujo, toma la URI y la redirige hacia la URL configurada.

Este Proxy Dummies maneja un concepto nuevo llamado **Reglas**. 
Se pueden crear una o muchas reglas para una misma URL. Cuando existen mas de una regla para una misma URL se toma la 
que tenga seleccionada la prioridad mas alta, La prioridad se basa en valor numerico entero positivo y mientras mayor 
sea su valor es la primer regla en ser evaluada y ejecutada si se cumplen dichas condiciones.

El Flujo en si mismo es muy simple 

Request ---> ProxyDummies ---> Tiene Rule Aplicable?
    
    |-> Si: Evaluo (n) rules y ejecuta la de mayor prioridad y devuelvo el dummy configurado
    ----------------------------------------------------------------------------------------------------
    |-> No: forwareo la request haciendo mirror del body y headers tanto de la request como del response.

**Como sabe el proxyDummies a donde redirigir la request si no hay rules aplicable?**

Dada la url a cual se le pega (al PD) el primer slash despues del prefix indica el Environment (proxyDummies/**environment_id**). 
Los Environment son la parte encarcargada de guardar hacia donde deben redirigirse las requests.


**Anatomia de una request**

http://localhost:8088/proxyDummies/bancon_soap_service/esb.EAI/TarjetasSvc

                     
< baseurl >/            
----------------------------< proxyDummiesUriPrefix >/

-------------------------------------------------< proxyDummiesEnvironment >/

------------------------------------------------------------------------------< Real Request Uri >/...

**Intalación** 

Se requiere descargar el war de acá: 
https://github.com/smsanta/proxyDummies/blob/master/releases/latest/proxyDummies.war

**PRE** 

Aantes de desplegar el war es necesario crear un usuario en su base de datos Oracle con el siguiente script.

    CREATE USER proxydummies
          IDENTIFIED BY password;
        
        GRANT create session TO proxydummies;
        GRANT create table TO proxydummies;
        GRANT create view TO proxydummies;
        GRANT create any trigger TO proxydummies;
        GRANT create any procedure TO proxydummies;
        GRANT create sequence TO proxydummies;
        GRANT create synonym TO proxydummies;
        
        ALTER USER proxydummies quota 50m on system;
        
        COMMIT;
        
**SETUP**

Si vamos a **/setup** encontraremos un dashboard que permite manejar las Rules y Environments de acuerdo a nuestras necesidades.

Ahí mismo se pueden dar de alta nuevas, modificar las que ya tengamos e incluso activar y desactivar rules umdividualmente.
Tambien puede activarse/desactivarse el saveResponses (En el header) para guardar las responses de todos las requests que
se forwarean.

Tambien se pueden dar de alta "**Environments**" los cuales se utilizan como un pseudo query string para dar redirección 
a las request que no cumplan con ninguna Rule. es la parte "**proxy**" del proxyDummies.

Por ultimo pero no menos importante esta la seccion se configuracion donde se puede setear la url de redireccion, 
las carpetas donde guardar los responses y otras opciones utiles.

**TODOs**
- Mejorar la interfaz en general.