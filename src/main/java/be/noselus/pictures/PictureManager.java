package be.noselus.pictures;

import java.io.InputStream;

import be.noselus.model.Person;
import be.noselus.repository.PoliticianRepositoryInDatabase;

public class PictureManager {

	public static InputStream get(int id) {
		
		Person politician = new PoliticianRepositoryInDatabase().getPoliticianById(id);
		
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
	
//	public static InputStream get(int id, int height, int width) {
//		
//		Person politician = new PoliticianRepositoryInDatabase().getPoliticianById(id);
//		
//		String path = null;
//		String ext = null;
//		if (id >= 77 && id <= 150) {
//			path = "/pictures/parlement/";
//			ext = ".jpg";
//		} else if (id >= 849 && id <= 998) {
//			path = "/pictures/chamber/";
//			ext = ".gif";
//		}
//		
//		if (path != null && ext != null) {
//			return PictureManager.class.getResourceAsStream(path + politician.assembly_id + ext);
//		} else {
//			return null;
//		}
//	}
	
}
