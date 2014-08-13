package be.noselus.repository;

import be.noselus.model.Assembly;
import be.noselus.util.dbutils.MapperBasedResultSetHandler;
import be.noselus.util.dbutils.MapperBasedResultSetListHandler;
import be.noselus.util.dbutils.QueryRunnerAdapter;
import be.noselus.util.dbutils.ResultSetMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class AssemblyRepositoryInDatabase implements AssemblyRepository, ResultSetMapper<Assembly> {

    private final QueryRunnerAdapter queryRunner;
    private final Cache<Integer, Assembly> assemblies = CacheBuilder.newBuilder().build();

    @Inject
    public AssemblyRepositoryInDatabase(final DataSource dataSource) {
        queryRunner = new QueryRunnerAdapter(dataSource);
    }

    @Override
    public Assembly findId(final int id) {
        final ResultSetHandler<Assembly> assemblyResultSetHandler = new MapperBasedResultSetHandler<>(this);
        Assembly assembly;
        try {
            assembly = assemblies.get(id, new Callable<Assembly>() {
                @Override
                public Assembly call() throws Exception {
                    return queryRunner.query("SELECT * FROM assembly WHERE id = ?;", assemblyResultSetHandler, id);
                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (assembly == null) {
            throw new RuntimeException("Error retrieving assembly with id " + id);
        }
        return assembly;
    }

    @Override
    public List<Assembly> getAssemblies() {
        final ResultSetHandler<List<Assembly>> assemblyResultSetHandler = new MapperBasedResultSetListHandler<>(this);
        final List<Assembly> assemblyList = queryRunner.query("SELECT * FROM assembly;", assemblyResultSetHandler);
        for (Assembly assembly : assemblyList) {
            assemblies.asMap().putIfAbsent(assembly.getId(), assembly);
        }
        return Lists.newArrayList(assemblies.asMap().values());
    }

    @Override
    public Assembly map(final ResultSet resultSet) throws SQLException {
        final int foundId = resultSet.getInt("id");
        final String label = resultSet.getString("label");
        final String level = resultSet.getString("level");

        return new Assembly(foundId, label, Assembly.Level.valueOf(level));
    }
}
