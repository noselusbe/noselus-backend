package be.noselus.repository;

import be.noselus.db.DatabaseHelper;
import be.noselus.model.Assembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AssemblyRegistryInDatabase extends AbstractRepositoryInDatabase implements AssemblyRegistry {

    private Map<Integer, Assembly> assemblies = new ConcurrentHashMap<>();

    @Inject
    public AssemblyRegistryInDatabase(final DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public Assembly findId(final int id) {
        if (!assemblies.containsKey(id)) {
            try (Connection db = dbHelper.getConnection(false, true);
                 PreparedStatement stat = db.prepareStatement("SELECT * FROM assembly WHERE id = ?;");) {

                stat.setInt(1, id);
                stat.execute();
                stat.getResultSet().next();
                final int foundId = stat.getResultSet().getInt("id");
                final String label = stat.getResultSet().getString("label");
                final String level = stat.getResultSet().getString("level");

                Assembly result = new Assembly(foundId, label, Assembly.Level.valueOf(level));

                assemblies.put(id, result);

            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving assembly with id " + id, e);
            }
        }
        return assemblies.get(id);
    }
}
