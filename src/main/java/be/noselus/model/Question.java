package be.noselus.model;

import be.noselus.search.HasIndexableDocument;
import be.noselus.search.SolrHelper;
import be.noselus.search.SolrHelper.Fields;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question implements HasIndexableDocument {

    private static final int EXCERPT_SIZE = 150;

    public Integer id;
    public int askedBy;
    public int askedTo;
    public int answeredBy;
    public String session;
    public Integer year;
    public String number;
    public LocalDate dateAsked;
    public LocalDate dateAnswered;
    public String title;
    public String questionText;
    public String answerText;
    public String excerpt;
    public Assembly assembly;
    public String assemblyRef;
    public List<Eurovoc> eurovocs = new ArrayList<>();
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Link original;

    public Question(Assembly assembly, String assemblyRef, String title) {
        this.assembly = assembly;
        this.assemblyRef = assemblyRef;
        this.title = title;
    }

    public Question(Integer id, int askedBy, int askedTo, int answeredBy, String session, Integer year, String number, LocalDate dateAsked, LocalDate dateAnswered, String title, String questionText, String answerText, Assembly assembly, String assemblyRef, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.askedBy = askedBy;
        this.askedTo = askedTo;
        this.answeredBy = answeredBy;
        this.session = session;
        this.year = year;
        this.number = number;
        this.dateAsked = dateAsked;
        this.dateAnswered = dateAnswered;
        this.title = title;
        if (questionText.length() < EXCERPT_SIZE) {
            this.excerpt = questionText;
        } else {
            this.excerpt = questionText.substring(0, EXCERPT_SIZE) + "...";
        }
        this.id = id;
        this.questionText = questionText;
        this.answerText = answerText;
        this.assembly = assembly;
        this.assemblyRef = assemblyRef;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.original = assembly.getLinkToQuestion(this);
    }

    public void addEurovoc(List<Eurovoc> list) {
        this.eurovocs = list;
    }

    public void addEurovoc(Eurovoc eurovoc) {
        this.eurovocs.add(eurovoc);
    }

    @Override
    public Map<Fields, Object> getIndexableFields() {
        Map<SolrHelper.Fields, Object> doc = new HashMap<>();
        putIfPresent(doc, this.title, SolrHelper.StringFields.TITLE_FR);
        putIfPresent(doc, this.questionText, SolrHelper.StringFields.QUESTION_FR);
        putIfPresent(doc, this.answerText, SolrHelper.StringFields.ANSWER_FR);
        putIfPresent(doc, this.dateAsked, SolrHelper.DateFields.DATE_ASKED);
        putIfPresent(doc, this.dateAnswered, SolrHelper.DateFields.DATE_ANSWERED);
        if (this.assembly != null) {
            doc.put(SolrHelper.StringFields.ASSEMBLY, this.assembly.getLabel());
        }
        if (this.askedBy > 0) {
            doc.put(SolrHelper.IntegerFields.ASKED_BY_ID, this.askedBy);
        }

        return doc;
    }

    private void putIfPresent(Map<SolrHelper.Fields, Object> map, Object value, SolrHelper.Fields field){
        if (value != null){
            map.put(field, value);
        }
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
                uriString = uriString + "federal.belgium.chamber:parliement";
                break;
            case REGION:
                uriString = uriString + "region.wallonia:parliement";
                break;
            default:
                throw new RuntimeException(String.valueOf(this.assembly.getLevel()) + " not supported yet");
        }

        uriString = uriString +
                ":written_question:" + this.id;

        return URI.create(uriString);
    }

    @Override
    public type getType() {
        return HasIndexableDocument.type.WRITTEN_QUESTION;

    }

}
