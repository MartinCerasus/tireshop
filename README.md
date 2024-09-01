## Kommentaarid:
Seisuga 01.09.24: Londoni broneering toimib.

Kasutatud on: java v11, tomcat v10, Jakarta Servlet, ubuntu masin

## Paigaldus
git clone git@github.com:MartinCerasus/tireshop.git

cd tireshop

javac -cp /opt/tomcat/lib/servlet-api.jar -d web/WEB-INF/classes src/main/java/com/example/servlet/*.java

cd web

jar -cvf ../build/tireshop.war *

mv ../build/tireshop.war /opt/tomcat/webapps/

http://serveri_aadress_v6i_ip:8080/tireshop

