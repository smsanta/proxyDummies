package proxydummies

class StaticScripts {

    final static String CREATE_DB_USER = ''' 
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
    '''
}
