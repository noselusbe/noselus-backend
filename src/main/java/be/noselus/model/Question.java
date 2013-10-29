package be.noselus.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

public class Question {

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
    public String assembly_ref;
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
}
