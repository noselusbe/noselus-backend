package be.noselus.util.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Map a ResultSet into an instance of T
 *
 * @param <T> the typed object in which the content of the ResultSet should be converted.
 */

public interface ResultSetMapper<T> {

    T map(ResultSet resultSet) throws SQLException;
}
