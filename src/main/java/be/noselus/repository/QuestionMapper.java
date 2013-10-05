package be.noselus.repository;

import be.noselus.model.Assembly;
import be.noselus.model.Question;
import org.joda.time.LocalDate;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestionMapper {

    AssemblyRegistry assemblyRegistry = new AssemblyRegistryInDatabase();

    public Question map( ResultSet r) throws SQLException {
        final int id = r.getInt("id");
        final int asked_by = r.getInt("asked_by");
        final int asked_to = r.getInt("asked_to");
        final int answered_by = r.getInt("answered_by");
        final String session = r.getString("session");
        final int year = r.getInt("year");
        final String number = r.getString("number");
        final LocalDate date_asked = LocalDate.fromDateFields(r.getDate("date_asked"));
        final Date date_answer = r.getDate("date_answer");
        final LocalDate date_answered;
        if (date_answer != null){
            date_answered = LocalDate.fromDateFields(date_answer);
        } else {
            date_answered = null;
        }
        final String title = r.getString("title");
        final String question_text = r.getString("question_text");
        final String answer_text = r.getString("answer_text");
        final int assembly_id = r.getInt("assembly_id");
        final Assembly assembly = assemblyRegistry.findId(assembly_id);
        return new Question(id, asked_by, asked_to, answered_by, session, year, number, date_asked, date_answered, title, question_text, answer_text, assembly);
    }
}