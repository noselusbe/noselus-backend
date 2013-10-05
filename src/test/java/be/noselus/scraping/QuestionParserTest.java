package be.noselus.scraping;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import be.noselus.model.Question;

public class QuestionParserTest {

	@Test
	public void openData() throws IOException {
		String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&id_doc=36256&type=all";
		Question qr = QuestionParser.parse(url);
		
		Assert.assertEquals("l'\"Open Data - Open Government\"", qr.title);
		Assert.assertEquals("2010-2011", qr.session);
		Assert.assertEquals(2011, qr.year.intValue());
		Assert.assertEquals("594 (2010-2011) 1", qr.number);
		Assert.assertEquals("2011-08-29", qr.dateAsked.toString());
		Assert.assertEquals("DISABATO Emmanuel", qr.askedBy.toString());
		Assert.assertEquals("FURLAN Paul, Ministre des Pouvoirs locaux et de la Ville", qr.askedTo.toString());
		Assert.assertEquals("2011-10-07", qr.dateAnswered.toString());
		Assert.assertEquals("FURLAN Paul", qr.answeredBy.toString());
		Assert.assertEquals(2596, qr.questionText.length());
		Assert.assertEquals(5663, qr.answerText.length());
	}
	
	@Test
	public void texts() throws IOException {
		String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&id_doc=36256&type=all";
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
		
		List<String> texts = QuestionParser.extract(doc, "div#print_container div + div");
		
		Assert.assertEquals(2596, texts.get(0).length());
		Assert.assertEquals(5663, texts.get(2).length());
	}
	
	@Test
	public void parseDate() {
		DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
			.appendDayOfMonth(2)
			.appendLiteral('/')
			.appendMonthOfYear(2)
			.appendLiteral('/')
			.appendYear(4, 4)
			.toFormatter();
		
		LocalDate tmp = LocalDate.parse("29/08/2011", dateFormatter);
		
		System.out.println(tmp);
	}
}
