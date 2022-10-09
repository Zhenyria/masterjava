set LB_HOME=C:\Java\liquibase-4.16.1
call %LB_HOME%\liquibase.bat --driver=org.postgresql.Driver ^
--classpath=%LB_HOME%\lib ^
--changeLogFile=databaseChangeLog.sql ^
--url="jdbc:postgresql://localhost:5432/masterjava" ^
--username=user ^
--password=password ^
migrate