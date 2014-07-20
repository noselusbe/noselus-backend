package be.noselus.repository;

import be.noselus.model.Assembly;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AssemblyRepositoryInDatabase extends AbstractRepositoryInDatabase implements AssemblyRepository {

    private Map<Integer, Assembly> assemblies = new ConcurrentHashMap<>();

    @Inject
    public AssemblyRepositoryInDatabase(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Assembly findId(final int id) {
        if (!assemblies.containsKey(id)) {
            try (Connection db = dataSource.getConnection();
                 PreparedStatement stat = db.prepareStatement("SELECT * FROM assembly WHERE id = ?;")) {

                stat.setInt(1, id);
                stat.execute();
                final ResultSet resultSet = stat.getResultSet();
                resultSet.next();
                Assembly result = assemblyMapping(resultSet);

                assemblies.put(id, result);

            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving assembly with id " + id, e);
            }
        }
        return assemblies.get(id);
    }

    @Override
    public List<Assembly> getAssemblies() {
        List<Assembly> result = new ArrayList<>();
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement("SELECT * FROM assembly;")) {

            stat.execute();
            final ResultSet resultSet = stat.getResultSet();
            while (resultSet.next()) {
                Assembly assembly = assemblyMapping(resultSet);
                result.add(assembly);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving assemblies", e);
        }
        return result;
    }

    private Assembly assemblyMapping(final ResultSet resultSet) throws SQLException {
        final int foundId = resultSet.getInt("id");
        final String label = resultSet.getString("label");
        final String level = resultSet.getString("level");

        return new Assembly(foundId, label, Assembly.Level.valueOf(level));
    }
}
