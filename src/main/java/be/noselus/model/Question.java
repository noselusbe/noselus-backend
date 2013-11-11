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
		if (this.title != null ) {
			doc.put(SolrHelper.StringFields.TITLE_FR, this.title);
		}
		if (this.question_text != null) {
			doc.put(SolrHelper.StringFields.QUESTION_FR, this.question_text);
		}
		if (this.answer_text != null) {
			doc.put(SolrHelper.StringFields.ANSWER_FR, this.answer_text);
		}
		if (this.date_asked != null) {
			doc.put(SolrHelper.DateFields.DATE_ASKED, this.date_asked);
		}
		if (this.date_answered != null) {
			doc.put(SolrHelper.DateFields.DATE_ANSWERED, this.date_answered);
		}
		if (this.assembly != null) {
			doc.put(SolrHelper.StringFields.ASSEMBLY, this.assembly.getLabel());
		}
		
		return doc;
	}

	@Override
	public URI getURI() {
		
		if (this.id == null) {
			throw new NullPointerException("Id must not be null");
		}
		
		if (this.assembly == null) {
			throw new NullPointerException("Assembly is null. Please avoid this!");
		}
		
		String uriString = "urn:x-noselusbe:";
		
		//until now, we just have documents from parliement, waloon region and 
		// parliement. Improve this when rewriting assembly class.
		switch (this.assembly.getLevel()) {
		case DEPUTY_CHAMBER:
		case FEDERAL:
			uriString = uriString + "federal.belgium:chamber.parliement";
			break;
		case REGION:
			uriString = uriString + "region.wallonia:parliement";
			break;
		default:
			try {
				throw new Exception(String.valueOf(this.assembly.getLevel()) + " not supported yet");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		uriString = uriString + 
				":written_question:" + this.id;
		
		
		URI u = URI.create(uriString);
		
		return u;
		
		
		
	}

	@Override
	public type getType() {
		return HasIndexableDocument.type.WRITTEN_QUESTION;

	}
}
