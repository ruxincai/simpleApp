Running the simpleApp Application
===============================

1. Install [Tomcat](http://tomcat.apache.org/)
1. Install [nodejs](https://nodejs.org/)
1. Install [PostgreSQL](http://postgresql.org/)
1. Disable password authentication for local connections:
	* edit `{PG_HOME}/data/pg_hba.conf`
	* go to the bottom of the file
	* change the "method" column (usually from "md5") to "trust" for all the rows
starting with "local"
1. Create your database called "homework":
	* `dropdb -U postgres homework` _(if necessary)_
	* `createdb -U postgres homework`
1. Open the project with [IntelliJ IDEA](https://www.jetbrains.com/idea/)
1. Run `ant download` either from IDEA or from the command line.
1. Build the project using IDEA.
	* IDEA will ask if it should compile TypeScript files, say "No".
1. At the command line run:
	* `cd target/web`
	* `npm install`
	* `npm run tsc-w`
1. The last process compiles TypeScript to JavaScript and stays active waiting
for changes.
1. Deploy the application in Tomcat (the easiest way is to create a file with the
content below and save it in {TOMCAT_HOME}/conf/Catalina/localhost/simpleApp.xml)
	* `<Context docBase="{SIMPLE_APP_DIRECTORY}/target/web"/>`
1. Start Tomcat
1. Open the application in a supported browsers (chrome/FF/Edge): `http://localhost:8080/simpleApp/`
1. Access with ssl configuration
	* https://tomcat.apache.org/tomcat-8.0-doc/ssl-howto.html
	* {java_home}/bin/keytool -genkey -alias tomcat -keyalg RSA
	* uncomment the connector setting from tomcat's server.xml configuraiton file.
	* '<Connector protocol="org.apache.coyote.http11.Http11NioProtocol"
			   port="8443" maxThreads="200"
			   scheme="https" secure="true" SSLEnabled="true"
			   keystoreFile="${user.home}/.keystore" keystorePass="changeit"
			   clientAuth="false" sslProtocol="TLS"/>'