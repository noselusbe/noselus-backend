package be.noselus.repository;

import be.noselus.model.Assembly;
import be.noselus.util.dbutils.MapperBasedResultSetHandler;
import be.noselus.util.dbutils.MapperBasedResultSetListHandler;
import be.noselus.util.dbutils.QueryRunnerAdapter;
import be.noselus.util.dbutils.ResultSetMapper;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AssemblyRepositoryDbUtils implements AssemblyRepository, ResultSetMapper<Assembly> {

    private final QueryRunnerAdapter queryRunner;

    @Inject
    public AssemblyRepositoryDbUtils(final DataSource dataSource) {
        queryRunner = new QueryRunnerAdapter(dataSource);
    }

    @Override
    public Assembly findId(final int id) {
        final ResultSetHandler<Assembly> assemblyResultSetHandler = new MapperBasedResultSetHandler<>(this);
        final Assembly assembly = queryRunner.query("SELECT * FROM assembly WHERE id = ?;", assemblyResultSetHandler, id);
        if (assembly == null) {
            throw new RuntimeException("Error retrieving assembly with id " + id);
        }
        return assembly;
    }

    @Override
    public List<Assembly> getAssemblies() {
        final ResultSetHandler<List<Assembly>> assemblyResultSetHandler = new MapperBasedResultSetListHandler<>(this);
        return queryRunner.query("SELECT * FROM assembly;", assemblyResultSetHandler);
    }

    @Override
    public Assembly map(final ResultSet resultSet) throws SQLException {
        final int foundId = resultSet.getInt("id");
        final String label = resultSet.getString("label");
        final String level = resultSet.getString("level");

        return new Assembly(foundId, label, Assembly.Level.valueOf(level));
    }
}
