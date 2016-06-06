package be.noselus.scraping;

import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WalloonRepresentativeDocument {

    private final Document webPage;
    private final Element pageContent;

    public WalloonRepresentativeDocument(final Document webPage) {
        this.webPage = webPage;
        pageContent = webPage.getElementById("page");
    }

    public String getName() {
        final Elements name = pageContent.select("h3.visible-xs");
        return name.get(0).text();
    }

    public String getParty() {
        final Elements party = pageContent.select("blockquote ul li span");
        return party.get(0).text();
    }

    public int getId() {
        final String location = webPage.location();
        final List<NameValuePair> parsedUrl = URLEncodedUtils.parse(location, Charsets.UTF_8);
        for (NameValuePair nameValuePair : parsedUrl) {
            if (nameValuePair.getName().equals("id")) {
                return Integer.valueOf(nameValuePair.getValue());
            }
        }
        throw new RuntimeException("Can't find the id of the representative " + location);
    }


    public String getAddress() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getPostalCode() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getTown() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getPhone() {
        final Elements listItems = pageContent.select("blockquote ul li");
        for (Element listItem : listItems) {
            final String text = listItem.text();
            if (text.contains("Tél :")) {
                String phoneNr;
                if (text.contains("Tél :")) {
                    phoneNr =  StringUtils.substringBetween(text, "Tél : ", "Fax :");
                } else {
                    phoneNr =  text.substring(text.lastIndexOf("Tél : ") + 6);
                }
                return phoneNr.trim();
            }
        }
        return "";
    }

    public String getFax() {
        final Elements listItems = pageContent.select("blockquote ul li");
        for (Element listItem : listItems) {
            final String text = listItem.text();
            if (text.contains("Fax :")) {
                return text.substring(text.lastIndexOf("Fax :") + 6);
            }
        }
        return "";
    }

    public String getEmail() {
        final Elements links = pageContent.select("blockquote ul li a");
        for (Element link : links) {
            String title = link.attr("title");
            if (title.equals("Courriel")) {
                String linkTarget = link.attr("abs:href");
                return linkTarget.replace("mailto:", "");
            }
        }
        return "";
    }
}
