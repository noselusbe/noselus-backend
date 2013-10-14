package be.noselus.db;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseHelper {

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
            instance.setUp();
        }
        return instance;
    }

    private BoneCP connectionPool;

    public void setUp() {
        DbConfig dbConfig = new DbConfig().invoke();
        try {
            // load the database driver (make sure this is in your classpath!)
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            // setup the connection pool
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(dbConfig.getUrl()); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(dbConfig.getUser());
            config.setPassword(dbConfig.getPassword());
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(18);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public Connection getConnection(boolean autoCommit, boolean readOnly) throws SQLException {
        final Connection connection = connectionPool.getConnection();
        connection.setAutoCommit(autoCommit);
//        connection.setReadOnly(readOnly);
        return connection; // fetch a connection
    }

    public void shutdown() {
        connectionPool.shutdown(); // shutdown connection pool.
    }

    private static class DbConfig {
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
                
                try {
                	user = System.getenv("DATABASE_USER");
                	password = System.getenv("DATABASE_PASSWORD");
                	url = database_url;
                } catch (Exception e) {
                	e.printStackTrace();
                }

            }
            return this;
        }
    }
}
