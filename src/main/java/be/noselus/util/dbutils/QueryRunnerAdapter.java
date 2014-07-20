package be.noselus.util.dbutils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.sql.SQLException;

public class QueryRunnerAdapter {

    private final QueryRunner runner;

    public QueryRunnerAdapter(final DataSource datasource) {
        this.runner = new QueryRunner(datasource);
    }

    public <T> T query(final String sql, final ResultSetHandler<T> rsh, final Object... params)  {
        try {
            return runner.query(sql, rsh, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T query(final String sql, final ResultSetHandler<T> rsh) {
        try {
            return runner.query(sql, rsh);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
