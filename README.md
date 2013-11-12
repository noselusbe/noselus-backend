Bulding
=======

In order to build this application you need:
* Java 7
* Maven 3
* PosgreSQL 9.1
* Solr 4.5

Set the environment variable "DATABASE_URL" with the URL to connect to the database, import the dump you can find in src/test/resources/dump in the database
Latest dump is: noselus.1048.backup

Alternatively, you may set values DATABASE_URL, DATABASE_USER and DATABASE_PASSWORD separately.

You must also run an instance of SOLR, used for indexing and search. A Solr `home` directory and installations instructions are available in `src/main/test/resources/solr/home/noselus.be_solr_how_to_start.md`

Configure the variable SOLR_URL (for instance: `http://localhost:8983/solr/noselusbe`) into environment.

To build the application:

    mvn clean install

