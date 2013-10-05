package be.noselus.repository;

import be.noselus.db.DatabaseHelper;
import be.noselus.model.Assembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AssemblyRegistryInDatabase implements AssemblyRegistry {

    private static final Logger logger = LoggerFactory.getLogger(AssemblyRegistryInDatabase.class);

    private Map<Integer, Assembly> assemblies = new ConcurrentHashMap<>();

    @Override
    public Assembly findId(final int id) {
        if (!assemblies.containsKey(id)){
            try {
                Connection db = DatabaseHelper.openConnection(false, true);
                PreparedStatement stat = db.prepareStatement("SELECT * FROM assembly where id = ?;");

                stat.setInt(1, id);
                stat.execute();
                stat.getResultSet().next();
                final int foundId = stat.getResultSet().getInt("id");
                final String label = stat.getResultSet().getString("label");
                final String level = stat.getResultSet().getString("level");

                Assembly result = new Assembly(foundId, label, Assembly.Level.valueOf(level));

                stat.close();
                db.close();

                assemblies.put(id,result);

            } catch (SQLException | ClassNotFoundException e) {
                logger.error("Error loading assembly from DB", e);
            }
        }
        return assemblies.get(id);
    }
}
