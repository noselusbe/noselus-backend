package be.noselus.util.dbutils;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapperBasedResultSetHandler<T> implements ResultSetHandler<T> {

    private final ResultSetMapper<T> mapper;

    public MapperBasedResultSetHandler(final ResultSetMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T handle(final ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        return mapper.map(resultSet);
    }
}
