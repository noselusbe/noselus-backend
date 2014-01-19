package be.noselus.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

public class Question {

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

    public Question() {

    }

    public Question(Integer id, int askedBy, int askedTo, int answeredBy, String session, Integer year, String number, LocalDate dateAsked, LocalDate dateAnswered, String title, String questionText, String answerText, Assembly assembly, String assemblyRef) {
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
    }
    
    public void addEurovoc(List<Eurovoc> list) {
    	this.eurovocs = list;
    }
    
    public void addEurovoc(Eurovoc eurovoc){
    	this.eurovocs.add(eurovoc);
    }
}
