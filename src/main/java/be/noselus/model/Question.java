package be.noselus.model;

import org.joda.time.LocalDate;

public class Question {

    private static final int EXCERPT_SIZE = 150;

    public Integer id;
    public PersonSmall asked_by;
    public PersonSmall asked_to;
    public PersonSmall answered_by;
    public String session;
    public Integer year;
    public String number;
    public LocalDate date_asked;
    public LocalDate date_answered;
    public String title;
    public String question_text;
    public String answer_Text;
    public String excerpt;

    public Question() {

    }

    public Question(PersonSmall asked_by, PersonSmall asked_to, PersonSmall answered_by, String session, Integer year, String number, LocalDate date_asked, LocalDate dateAnswered, String title, String question_text, String answer_Text, Integer id) {
        this.asked_by = asked_by;
        this.asked_to = asked_to;
        this.answered_by = answered_by;
        this.session = session;
        this.year = year;
        this.number = number;
        this.date_asked = date_asked;
        this.date_answered = dateAnswered;
        this.title = title;
        this.question_text = question_text;
        this.answer_Text = answer_Text;
        if (question_text.length() < EXCERPT_SIZE) {
            this.excerpt = question_text;
        } else {
            this.excerpt = question_text.substring(0, EXCERPT_SIZE) + "...";
        }
        this.id = id;
    }
}
