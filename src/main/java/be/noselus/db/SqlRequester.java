package be.noselus.db;

import be.noselus.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlRequester {

    private static final Logger logger = LoggerFactory.getLogger(SqlRequester.class);

    public static void insertOrUpdateQuestion(Connection db, Question question) throws SQLException {
        if (questionIsPresent(db, question)) {
            updateQuestion(db, question);
        } else {
            insertQuestion(db, question);
        }

    }

    private static void insertQuestion(final Connection db, final Question question) throws SQLException {
        logger.debug("Inserting question " + question.assembly.getLabel() + " " + question.id);

        String sql =
                "INSERT INTO written_question (session, year, number, date_asked, date_answer, title, question_text, answer_text, asked_by, asked_to, assembly_ref, assembly_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stat = db.prepareStatement(sql);
        int idx = 1;
        stat.setString(idx++, question.session);
        stat.setInt(idx++, question.year);
        stat.setString(idx++, question.number);
        stat.setDate(idx++, new java.sql.Date(question.date_asked.toDate().getTime()));
        if (question.date_answered != null) {
            stat.setDate(idx++, new java.sql.Date(question.date_answered.toDate().getTime()));
        } else {
            stat.setNull(idx++, java.sql.Types.DATE);
        }
        stat.setString(idx++, question.title);
        stat.setString(idx++, question.question_text);
        stat.setString(idx++, question.answer_text);
        stat.setInt(idx++, question.asked_by);
        stat.setInt(idx++, question.asked_to);
        stat.setString(idx++, question.id.toString());
        stat.setInt(idx++, question.assembly.getId());

        stat.execute();
        stat.close();
    }

    private static void updateQuestion(final Connection db, final Question question) throws SQLException {
        logger.debug("Updating question " + question.assembly.getLabel() + " " + question.id);

        String sql =
                "UPDATE written_question SET session = ?,  year = ? , number = ?, date_asked = ?, date_answer = ?, title = ?, question_text = ?, answer_text = ?, asked_by = ?, asked_to = ? "
                        + "WHERE assembly_ref = ? AND assembly_id = ?";
        PreparedStatement stat = db.prepareStatement(sql);
        int idx = 1;
        stat.setString(idx++, question.session);
        stat.setInt(idx++, question.year);
        stat.setString(idx++, question.number);
        stat.setDate(idx++, new java.sql.Date(question.date_asked.toDate().getTime()));
        if (question.date_answered != null) {
            stat.setDate(idx++, new java.sql.Date(question.date_answered.toDate().getTime()));
        } else {
            stat.setNull(idx++, java.sql.Types.DATE);
        }
        stat.setString(idx++, question.title);
        stat.setString(idx++, question.question_text);
        stat.setString(idx++, question.answer_text);
        stat.setInt(idx++, question.asked_by);
        stat.setInt(idx++, question.asked_to);
        stat.setString(idx++, question.id.toString());
        stat.setInt(idx++, question.assembly.getId());

        stat.execute();
        stat.close();
    }

    private static boolean questionIsPresent(final Connection db, final Question question) throws SQLException {
        logger.debug("Checking if question is present " + question.assembly.getLabel() + " " + question.id);
        String sql = "SELECT id FROM written_question WHERE assembly_ref = ? AND assembly_id = ?";
        PreparedStatement stat = db.prepareStatement(sql);
        int idx = 1;
        stat.setString(idx++, question.id.toString());
        stat.setInt(idx++, question.assembly.getId());
        stat.execute();
        return stat.getResultSet().next();
    }

    public static void updatePersonAssemblyId(Connection db, String name, int id) throws SQLException {
        String sql = "UPDATE person SET assembly_id = ? WHERE full_name LIKE ?;";
        PreparedStatement stat = db.prepareStatement(sql);

        stat.setInt(1, id);
        stat.setString(2, name.trim().replace(' ', ' '));

        stat.execute();
        stat.close();
    }
}
