package be.noselus.pictures;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import be.noselus.db.DatabaseHelper;
import be.noselus.repository.PoliticianRepository;

import com.google.inject.Inject;

public class PictureManager {


    private Map<Integer, Integer> mapping = null;

    PoliticianRepository politicianRepository;

    @Inject
    public PictureManager(final PoliticianRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
        Connection db = null;
        try {
            db = DatabaseHelper.getInstance().getConnection(false, true);

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
            throw new RuntimeException(e);
        }
    }

    public InputStream get(int id) {

        String path = null;
        String ext = null;
        if (id >= 77 && id <= 150) {
            path = "/pictures/parlement/";
            ext = ".jpg";
        } else if (id >= 151 && id <= 158) {
            path = "/pictures/minister/";
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
    
    public void get(int id, int width, int height, OutputStream os) throws IOException {
        String path = null;
        String ext = null;
        if (id >= 77 && id <= 150) {
            path = "/pictures/parlement/";
            ext = ".jpg";
        } else if (id >= 151 && id <= 158) {
            path = "/pictures/minister/";
            ext = ".jpg";        	
        } else if (id >= 849 && id <= 998) {
            path = "/pictures/chamber/";
            ext = ".gif";
        }

        if (path != null && ext != null) {
            Thumbnails.of(PictureManager.class.getResourceAsStream(path + mapping.get(id) + ext))
            .size(width, height)
            .crop(Positions.CENTER)
        	.outputFormat("jpg")
        	.toOutputStream(os);
        }
    }

}
