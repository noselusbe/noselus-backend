package be.noselus.tools;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import be.noselus.NosElusModule;
import be.noselus.model.Question;
import be.noselus.repository.QuestionRepository;
import be.noselus.search.SolrHelper;



public class ExistingQuestionIndexer {

	public static void main(String[] args) throws SolrServerException, IOException {
		Injector injector = Guice.createInjector(new NosElusModule());
		QuestionRepository  qr = injector.getInstance(QuestionRepository.class);
		
		boolean continueIndexing = true;
		final int limit = 50;
		int offset = 0;
		
		do {
			List<Question> questions = qr.getQuestions(limit, offset);
			
			Iterator<Question> i = questions.iterator();
			while (i.hasNext()) {
				Question q = i.next();
				SolrHelper.add(q, false);
			}
			
			if (questions.size() == 50) {
				offset = offset + questions.size();
			} else {
				continueIndexing = false;
			}
			
			
			
		} while (continueIndexing) ;
		
		SolrHelper.getSolrServer().commit();
	}
	
}
