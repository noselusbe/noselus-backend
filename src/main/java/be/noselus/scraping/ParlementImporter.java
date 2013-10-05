package be.noselus.scraping;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.noselus.db.DatabaseHelper;
import be.noselus.db.SqlRequester;
import be.noselus.model.Question;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.PoliticianRepositoryInDatabase;

public class ParlementImporter {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		PoliticianRepository deputyRepository = new PoliticianRepositoryInDatabase();
		QuestionParser parser = new QuestionParser(deputyRepository);
		
		String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&type=all&id_doc=";
		
		Connection db = DatabaseHelper.openConnection(false, true); 
		
		List<Question> questions = new ArrayList<>();
		for (int id = 50000; id < 50005; id++) {
			try {
				questions.add(parser.parse(id));
				Thread.sleep(1500);
			} catch (IOException | IllegalArgumentException | InterruptedException e) {
				System.out.println(url + id);
				e.printStackTrace();
			}
		}
		
		for (Question question : questions) {
			SqlRequester.insertQuestion(db, question);
		}
		
		db.commit();
		db.close();
	}
	
}
