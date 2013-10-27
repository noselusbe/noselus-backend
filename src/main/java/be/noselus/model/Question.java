package be.noselus.model;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import be.noselus.search.HasIndexableDocument;

import be.noselus.search.SolrHelper.Fields;
public class Question implements HasIndexableDocument {

    private static final int EXCERPT_SIZE = 150;



import be.noselus.search.HasIndexableDocument;
import be.noselus.search.SolrHelper;

public class Question implements HasIndexableDocument {

    private static final int EXCERPT_SIZE = 150;


    public Integer id;
    public int asked_by;
    public int asked_to;
    public int answered_by;
    public String session;
    public Integer year;
    public String number;
    public LocalDate date_asked;
    public LocalDate date_answered;
    public String title;
    public String question_text;
    public String answer_text;
    public String excerpt;
    public Assembly assembly;
    public List<Eurovoc> eurovocs = new ArrayList<Eurovoc>();

    public Question() {

    }


    public Question(Integer id, int asked_by, int asked_to, int answered_by, String session, Integer year, String number, LocalDate date_asked, LocalDate dateAnswered, String title, String questionText, String answerText, Assembly assembly) {
        this.asked_by = asked_by;
        this.asked_to = asked_to;
        this.answered_by = answered_by;
        this.session = session;
        this.year = year;
        this.number = number;
        this.date_asked = date_asked;
        this.date_answered = dateAnswered;
        this.title = title;
        if (questionText.length() < EXCERPT_SIZE) {
            this.excerpt = questionText;
        } else {
            this.excerpt = questionText.substring(0, EXCERPT_SIZE) + "...";
        }
        this.id = id;
        this.question_text = questionText;
        this.answer_text = answerText;
        this.assembly = assembly;
    }
    
    public void addEurovoc(List<Eurovoc> list) {
    	this.eurovocs = list;
    }
    
    public void addEurovoc(Eurovoc eurovoc){
    	this.eurovocs.add(eurovoc);
    }

	@Override
	public Map<Fields, Object> getIndexableFields() {
		Map<SolrHelper.Fields, Object> doc = new HashMap<SolrHelper.Fields, Object>();
		doc.put(SolrHelper.StringFields.TITLE, this.title);
		doc.put(SolrHelper.StringFields.QUESTION_FR, this.question_text);
		doc.put(SolrHelper.StringFields.ANSWER_FR, this.answer_text);
		doc.put(SolrHelper.DateFields.DATE_ASKED, this.date_asked);
		doc.put(SolrHelper.DateFields.DATE_ANSWERED, this.date_answered);
		
		return doc;
	}

	@Override
	public URI getURI() {
		
		if (this.id == null) {
			throw new NullPointerException("Id must not be null");
		}
		
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("MD5");
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String mdid = this.id.byteValue() + 
				String.valueOf(this.getType());
		URI u = URI.create("urn:md5:" + md.digest(mdid.getBytes()));
		
		return u;
		
		
		
	}

	@Override
	public type getType() {
		return HasIndexableDocument.type.WRITTEN_QUESTION;

	}
}
