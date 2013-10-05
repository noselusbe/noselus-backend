package be.noselus.db;

import be.noselus.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlRequester {

	public static void insertQuestion(Connection db, Question question) throws SQLException {

		String sql = 
				"INSERT INTO written_question (session, year, number, date_asked, date_answer, title, question_text, answer_text, asked_by, asked_to, assembly_ref) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
		stat.setInt(idx++, question.id);
		
		stat.execute();
		stat.close();
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
