package be.noselus.pictures;

import be.noselus.db.DatabaseHelper;
import be.noselus.model.Person;
import be.noselus.repository.PoliticianRepository;
import com.google.inject.Inject;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class PictureManager {
	
	private static PictureManager singleton = null;
	
	private static Map<Integer, Integer> mapping = null;
	
	private PictureManager() throws SQLException, ClassNotFoundException {
		Connection db = DatabaseHelper.openConnection(false, true);
		
		PreparedStatement stat = db.prepareStatement("SELECT id, assembly_id FROM person;");
		stat.execute();
		
		while (stat.getResultSet().next()) {
			int id = stat.getResultSet().getInt("id");
			int assembly_id = stat.getResultSet().getInt("assembly_id");
			mapping.put(id, assembly_id);
		}
		
		stat.close();
		db.close();
	}
	
	public static PictureManager get() throws SQLException, ClassNotFoundException {
		if (singleton == null) {
			singleton = new PictureManager();
		}
		return singleton;
	}

    PoliticianRepository politicianRepository;

    @Inject
    public PictureManager(final PoliticianRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
    }

    public InputStream get(int id) {
		
		Person politician = politicianRepository.getPoliticianById(id);
		
		String path = null;
		String ext = null;
		if (id >= 77 && id <= 150) {
			path = "/pictures/parlement/";
			ext = ".jpg";
		} else if (id >= 849 && id <= 998) {
			path = "/pictures/chamber/";
			ext = ".gif";
		}

		if (path != null && ext != null) {
			return PictureManager.class.getResourceAsStream(path + mapping.get(id) + ext);
		} else {
			return null;
		}
	}
	
}
