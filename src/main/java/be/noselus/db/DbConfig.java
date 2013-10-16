package be.noselus.db;

import java.net.URI;
import java.net.URISyntaxException;

class DbConfig {
    private String url;
    private String user;
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public DbConfig invoke() {
        final String database_url = System.getenv("DATABASE_URL");


        if (database_url == null) {
            url = "jdbc:postgresql://localhost:5432/";
        } else {
            user = System.getenv("DATABASE_USER");
            password = System.getenv("DATABASE_PASSWORD");
            url = database_url;

            if (user == null && password == null) {
                URI dbUri = null;
                try {
                    dbUri = new URI(database_url);
                    user = dbUri.getUserInfo().split(":")[0];
                    password = dbUri.getUserInfo().split(":")[1];
                    url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

            }

        }
        return this;
    }
}
