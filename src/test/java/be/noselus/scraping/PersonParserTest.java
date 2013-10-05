package be.noselus.scraping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class PersonParserTest {

	String url = "http://www.parlement-wallon.be/content/default.php?p=01-01";
	
	@Test
	public void parseList() throws MalformedURLException, IOException {
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
		
		Elements list = doc.select("td.parl_liste");
		
        for (Element item: list) {
        	Elements text = item.select("div.zone_texte a[href]");
        	String name = StringEscapeUtils.unescapeHtml(text.text());
        	System.out.println(name);
        	
        	Elements photo = item.select("div.zone_photo a img");
        	String pic = StringEscapeUtils.unescapeHtml(photo.attr("src"));
        	pic = pic.replace("/web/", "/webhd/");
        	System.out.println(pic);
        	
//        	String id = pic.substring(pic.lastIndexOf('/') + 1, pic.length() - 4); 
//        	saveImage(pic, "src/main/resources/pictures/person/" + id + ".jpg");
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
