# Java EE példa

## DataSource beállítása

A `jboss-cli.bat` állományt elindítva a parancssorba beírandó:

    connect localhost
    deploy "~/mysql-connector-java-8.0.12.jar"
    /subsystem=datasources:installed-drivers-list
    data-source add --name=employeeds --jndi-name=java:/jdbc/employeeds --driver-name=mysql-connector-java-8.0.12.jar --connection-url=jdbc:mysql://localhost/employees?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC --user-name=employees --password=employees

/subsystem=datasources:read-resource