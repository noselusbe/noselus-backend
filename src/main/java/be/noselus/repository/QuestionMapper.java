package be.noselus.repository;

import be.noselus.model.Assembly;
import be.noselus.model.Question;
import com.google.inject.Inject;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class QuestionMapper {

    private final AssemblyRegistry assemblyRegistry;

    @Inject
    public QuestionMapper(AssemblyRegistry assemblyRegistry) {
        this.assemblyRegistry = assemblyRegistry;
    }

    public Question map(ResultSet r) throws SQLException {
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
        if (date_answer != null) {
            date_answered = LocalDate.fromDateFields(date_answer);
        } else {
            date_answered = null;
        }
        final String title = capitalizeFirstLetter(r.getString("title"));
        final String question_text = r.getString("question_text");
        final String answer_text = r.getString("answer_text");
        final int assembly_id = r.getInt("assembly_id");
        final Assembly assembly = assemblyRegistry.findId(assembly_id);
        final String assemblyRef = r.getString("assembly_ref");
        final Timestamp createdAtDb = r.getTimestamp("created_at");
        final LocalDateTime createdAt;
        if (createdAtDb == null){
            createdAt = null;
        } else {
            createdAt = LocalDateTime.fromDateFields(createdAtDb);
        }
        final Timestamp updatedAtDb = r.getTimestamp("updated_at");
        final LocalDateTime updatedAt;
        if (updatedAtDb == null){
            updatedAt = null;
        } else {
            updatedAt = LocalDateTime.fromDateFields(updatedAtDb);
        }
        return new Question(id, asked_by, asked_to, answered_by, session, year, number, date_asked, date_answered, title, question_text, answer_text, assembly, assemblyRef, createdAt,updatedAt);
    }

    private String capitalizeFirstLetter(String original) {
        return Character.toUpperCase(original.charAt(0)) + original.substring(1);
    }
}