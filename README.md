Bulding
=======

In order to build this application you need:
* Java 7
* Maven 3
* PosgreSQL 9.2

Set the environment variable "DATABASE_URL" with the URL to connect to the database, import the dump you can find in src/test/resources/dump in the database
Latest dump is: noselus.1048.backup

To build the application:

    mvn clean install
