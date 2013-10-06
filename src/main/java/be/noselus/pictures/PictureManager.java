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
import java.util.TreeMap;

public class PictureManager {


    private Map<Integer, Integer> mapping = null;

    PoliticianRepository politicianRepository;

    @Inject
    public PictureManager(final PoliticianRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
        Connection db = null;
        try {
            db = DatabaseHelper.openConnection(false, true);

            PreparedStatement stat = db.prepareStatement("SELECT id, assembly_id FROM person;");
            stat.execute();

            mapping = new TreeMap<Integer, Integer>();
            while (stat.getResultSet().next()) {
                int id = stat.getResultSet().getInt("id");
                int assembly_id = stat.getResultSet().getInt("assembly_id");
                mapping.put(id, assembly_id);
            }

            stat.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
