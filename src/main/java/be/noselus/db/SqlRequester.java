package be.noselus.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SqlRequester {

    private SqlRequester() {
    }

    public static void updatePersonAssemblyId(Connection db, String name, int id) throws SQLException {
        String sql = "UPDATE person SET assembly_id = ? WHERE full_name LIKE ?;";
        try (PreparedStatement stat = db.prepareStatement(sql);) {

            stat.setInt(1, id);
            stat.setString(2, name.trim().replace(' ', ' '));

            stat.execute();
        }
    }
}
