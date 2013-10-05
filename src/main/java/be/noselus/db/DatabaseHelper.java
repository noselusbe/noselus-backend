package be.noselus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

	public static Connection openConnection(boolean autoCommit, boolean readOnly) throws SQLException, ClassNotFoundException {
		String url = "jdbc:postgresql://hackathon01.cblue.be:5432/noselus?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		String user = "noselus2";
		String password = "noselus";
		
		Class.forName("org.postgresql.Driver");
		Connection db = DriverManager.getConnection(url, user, password);
		
		db.setAutoCommit(autoCommit);
		db.setReadOnly(readOnly);
		
		return db;
	}
	
}
