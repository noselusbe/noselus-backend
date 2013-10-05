package be.noselus.db;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    public static Connection openConnection(boolean autoCommit, boolean readOnly) throws SQLException, ClassNotFoundException {
        final String database_url = System.getenv("DATABASE_URL");

        String url;
        String user;
        String password;

        if (database_url == null) {
            url = "jdbc:postgresql://hackathon01.cblue.be:5432/noselus?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            user = "noselus2";
            password = "noselus";

        } else {
            URI dbUri = null;
            try {
                dbUri = new URI(database_url);
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            user = dbUri.getUserInfo().split(":")[0];
            password = dbUri.getUserInfo().split(":")[1];
            url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        }

        Class.forName("org.postgresql.Driver");
        Connection db = DriverManager.getConnection(url, user, password);

        db.setAutoCommit(autoCommit);
        db.setReadOnly(readOnly);

        return db;
    }

}
