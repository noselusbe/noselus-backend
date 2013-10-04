package be.noselus.model;

import org.joda.time.LocalDate;

public class Question {
    public Person askedBy;
    public Person askedTo;
    public Person answeredBy;
    public String session;
    public Integer year;
    public String number;
    public LocalDate dateAsked;
    public LocalDate dateAnswered;
    public String title;
    public String questionText;
    public String answerText;

    public Question( Person askedBy,  Person askedTo, Person answeredBy, String session, Integer year, String number, LocalDate dateAsked, LocalDate dateAnswered, String title, String questionText, String answerText) {
        this.askedBy = askedBy;
        this.askedTo = askedTo;
        this.answeredBy = answeredBy;
        this.session = session;
        this.year = year;
        this.number = number;
        this.dateAsked = dateAsked;
        this.dateAnswered = dateAnswered;
        this.title = title;
        this.questionText = questionText;
        this.answerText = answerText;
    }
}
