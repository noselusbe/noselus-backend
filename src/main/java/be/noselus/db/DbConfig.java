package be.noselus.db;

import com.google.inject.Singleton;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Singleton
public class DbConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbConfig.class);

    private String driver;
    private String url;
    private String username;
    private String password;

    public DbConfig() {
    }

    public DbConfig(final String driver, final String url, final String username, final String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(final String driver) {
        this.driver = driver;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public DbConfig invoke() {
        if (driver == null) {
            final DynamicPropertyFactory dynamicPropertyFactory = DynamicPropertyFactory.getInstance();
            DynamicStringProperty driverClass = dynamicPropertyFactory.
                    getStringProperty("DATABASE_DRIVER", "org.postgresql.Driver");
            driver = driverClass.get();
            DynamicStringProperty databaseUrl = dynamicPropertyFactory.
                    getStringProperty("DATABASE_URL", "jdbc:postgresql://localhost:5432/");

            url = databaseUrl.get();
            DynamicStringProperty databaseUsername = dynamicPropertyFactory.getStringProperty("DATABASE_USER", null);
            DynamicStringProperty databasePassword = dynamicPropertyFactory.getStringProperty("DATABASE_PASSWORD", null);

            username = databaseUsername.get();
            password = databasePassword.get();

            if (username == null && password == null) {
                URI dbUri;
                try {
                    dbUri = new URI(url);
                    username = dbUri.getUserInfo().split(":")[0];
                    password = dbUri.getUserInfo().split(":")[1];
                    url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                } catch (Exception e) {
                    LOGGER.error("error connecting to database", e);
                }
            }
        }
        return this;
    }
}
