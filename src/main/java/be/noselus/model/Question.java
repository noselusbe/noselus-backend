package be.noselus.model;

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

    public Question() {

    }

    public Question(PersonSmall asked_by, PersonSmall asked_to, PersonSmall answered_by, String session, Integer year, String number, LocalDate date_asked, LocalDate dateAnswered, String title, String question_text, String answer_text, Integer id) {
        this.asked_by = asked_by.id;
        this.asked_to = asked_to.id;
        this.answered_by = answered_by.id;
        this.session = session;
        this.year = year;
        this.number = number;
        this.date_asked = date_asked;
        this.date_answered = dateAnswered;
        this.title = title;
        this.question_text = question_text;
        this.answer_text = answer_text;
        if (question_text.length() < EXCERPT_SIZE) {
            this.excerpt = question_text;
        } else {
            this.excerpt = question_text.substring(0, EXCERPT_SIZE) + "...";
        }
        this.id = id;
    }
}
