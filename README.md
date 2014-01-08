Build status: [![Build Status](http://jenkins.hurion.eu/buildStatus/icon?job=nos elus - backend)](http://jenkins.hurion.eu/job/nos%20elus%20-%20backend/)

Bulding
=======

In order to build this application you need:
* [Java 7](http://java.com/en/download/index.jsp). If you're using a mac [Java 7 on mac](http://www.monkehworks.com/set-java-7-as-default-jvm-on-mac-osx-mountain-lion)
* [Maven 3.1.1](http://maven.apache.org/download.cgi)
* [PosgreSQL 9.2](http://www.postgresql.org/download/). If you're on a mac we recommend [Posgresql.app](http://postgresapp.com/)

Set the environment variable "DATABASE_URL" with the URL to connect to the database, import the dump you can find in src/test/resources/dump in the database
Latest dump is: noselus.1048.backup

To build the application:

    mvn clean install

