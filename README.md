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
    |-> No: forwareo la request haciendo mirror del body y header tanto de la request como del response.

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

Si vamos a **/setup** encontraremos un dashboard que permite manejar las Rules de acuerdo a nuestras necesidades.

Ahí mismo se pueden dar de alta nuevas, modificar las que ya tengamos e incluso activar y desactivar rules umdividualmente.
Tambien puede activarse/desactivarse el saveResponses (En el header) para guardar las responses de todos las requests que
se forwarean.


Por ultimo pero no menos importante esta la seccion se configuracion donde se puede setear la url de redireccion, 
las carpetas donde guardar los responses y otras opciones utiles.

**NOTAS:** 

El scope final es que se puedan configurar cualquier tipo de request tanto SOAP como REST pero a priori la 
implementacion se limita a trabajar solamente con SOAP. 

Y tambien que las rules tengan la autonomia de definirse autosuficientes, es decir que ellas mismas sepan si tienen que 
hacer un POST/GET o si es un SOAP/REST y hacia donde va a redirigir las llamadas.

**TODOs**
- Agregar soporte para REST
- Agregar "ambientes" que son las urls finales donde las request van a ser redirigidas(Implica eliminar la config unica de redireccion) y ser asignadas a cara rule por separado.
- Mejorar la interfaz en general.