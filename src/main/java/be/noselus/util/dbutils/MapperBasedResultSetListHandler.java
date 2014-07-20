package be.noselus.util.dbutils;

import org.apache.commons.dbutils.handlers.AbstractListHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapperBasedResultSetListHandler<T> extends AbstractListHandler<T> {

    private final ResultSetMapper<T> mapper;

    public MapperBasedResultSetListHandler(final ResultSetMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    protected T handleRow(final ResultSet resultSet) throws SQLException {
        return mapper.map(resultSet);
    }

}
