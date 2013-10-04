package be.noselus.scraping;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import be.noselus.model.QuestionResponse;

public class JSoupTest {

	@Test
	@Ignore
	public void openData() throws IOException {
		String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&id_doc=36256&type=all";
		QuestionResponse qr = QuestionResponseParser.parse(url);
		
		Assert.assertEquals("l'\"Open Data - Open Government\"", qr.title);
		Assert.assertEquals("Session : 2010-2011", qr.session);
		Assert.assertEquals("Année : 2011", qr.year);
		Assert.assertEquals("N° : 594 (2010-2011) 1", qr.number);
		Assert.assertEquals("Question écrite du 29/08/2011 ", qr.question_date);
		Assert.assertEquals("de DISABATO Emmanuel", qr.question_from);
		Assert.assertEquals("à FURLAN Paul, Ministre des Pouvoirs locaux et de la Ville", qr.question_to);
		Assert.assertEquals("Réponse du 07/10/2011 ", qr.response_date);
		Assert.assertEquals("de FURLAN Paul", qr.response_from);
		Assert.assertEquals(2596, qr.questionText.length());
		Assert.assertEquals(5663, qr.responseText.length());
	}
	
	@Test
	public void texts() throws IOException {
		String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&id_doc=36256&type=all";
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
		
		List<String> texts = QuestionResponseParser.extract(doc, "div#print_container div + div");
		
		Assert.assertEquals(2596, texts.get(0).length());
		Assert.assertEquals(5663, texts.get(2).length());
	}
}
