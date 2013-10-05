package be.noselus.model;

import org.joda.time.LocalDate;

public class Question {
    public PersonSmall asked_by;
    public PersonSmall asked_to;
    public PersonSmall answered_by;
    public String session;
    public Integer year;
    public String number;
    public LocalDate date_asked;
    public LocalDate date_answered;
    public String title;
    public String questionText;
    public String answerText;

    public Question() {
    	
    }
    
    public Question( PersonSmall asked_by,  PersonSmall asked_to, PersonSmall answered_by, String session, Integer year, String number, LocalDate date_asked, LocalDate dateAnswered, String title, String questionText, String answerText) {
        this.asked_by = asked_by;
        this.asked_to = asked_to;
        this.answered_by = answered_by;
        this.session = session;
        this.year = year;
        this.number = number;
        this.date_asked = date_asked;
        this.date_answered = dateAnswered;
        this.title = title;
        this.questionText = questionText;
        this.answerText = answerText;
    }
}
