package be.noselus.pictures;

import be.noselus.model.Person;
import be.noselus.repository.PoliticianRepository;
import com.google.inject.Inject;

import java.io.InputStream;

public class PictureManager {

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
			return PictureManager.class.getResourceAsStream(path + politician.assembly_id + ext);
		} else {
			return null;
		}
	}
	
}
