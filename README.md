Build status: [![Build Status](http://jenkins.hurion.eu/buildStatus/icon?job=nos elus - backend)](http://jenkins.hurion.eu/job/nos%20elus%20-%20backend/)

Bulding
=======

In order to build this application you need:
* [Java 7](http://java.com/en/download/index.jsp). If you're using a mac [Java 7 on mac](http://www.monkehworks.com/set-java-7-as-default-jvm-on-mac-osx-mountain-lion)
* [Maven 3.1.1](http://maven.apache.org/download.cgi)
* [PosgreSQL 9.2](http://www.postgresql.org/download/). If you're on a mac we recommend [Posgresql.app](http://postgresapp.com/)

Once all installed, create a database.

Set the environment variable ```DATABASE_URL```, ```DATABASE_USER```, and ```DATABASE_PASSWORD``` with the URL to connect to the database,
import the dump you can find in ```src/test/resources/dump``` in the database
Latest dump is: ```noselus.20130111.backup```
Then clone the repository:

    git clone https://github.com/noselusbe/noselus-backend.git

You can launch the server with

    mvn package exec:java

To build the application:

    mvn clean install

If you want to install the application to another computer you need to go to the target directory and copy the ```be.noselus-{VERSION}.jar``` as well as the ```lib``` directory and all it's content
 to the target location.
You can then run the application with ```java -jar be.noselus-{VERSION}.jar```

The application will then be accessible at ```http://localhost:4567/``` for example, the list of most recent questions is accessible at ```http://localhost:4567/questions```

The documentation of the api will come soon.
