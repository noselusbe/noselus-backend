package be.noselus.scraping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.noselus.db.DatabaseHelper;
import be.noselus.db.SqlRequester;

public class PersonParserTest {

	String url = "http://www.parlement-wallon.be/content/default.php?p=01-01";
	Connection db;
	
	
	@Before
	public void init() throws ClassNotFoundException, SQLException {
		db = DatabaseHelper.openConnection(true, false);
	}
	
	@After
	public void tearDown() throws SQLException {
		db.close();
	}
	
	@Test
	public void parseList() throws MalformedURLException, IOException {
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
		
		Elements list = doc.select("td.parl_liste");
		
        for (Element item: list) {
        	try {
	        	Elements text = item.select("div.zone_texte a[href]");
	        	String name = StringEscapeUtils.unescapeHtml(text.text());
	        	System.out.println(name);
	        	
	        	Elements photo = item.select("div.zone_photo a img");
	        	String pic = StringEscapeUtils.unescapeHtml(photo.attr("src"));
	        	pic = pic.replace("/web/", "/webhd/");
	        	System.out.println(pic);
	        	
	        	String id = pic.substring(pic.lastIndexOf('/') + 1, pic.length() - 4); 
	//        	saveImage(pic, "src/main/resources/pictures/person/" + id + ".jpg");
	        	
	        	SqlRequester.updatePersonAssemblyId(db, name, Integer.valueOf(id));
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }
		
	}
	
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}
	
}
