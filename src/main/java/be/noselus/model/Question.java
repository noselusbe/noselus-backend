package be.noselus.model;

import org.joda.time.LocalDate;

public class Question {
    public Person askedBy;
    public Person askedTo;
    public Person answeredBy;
    public String session;
    public Integer year;
    public Integer number;
    public LocalDate dateAsked;
    public LocalDate dateAnswered;
    public String title;
    public String questionText;
    public String answerText;
}
