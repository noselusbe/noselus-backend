package be.noselus.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import be.noselus.model.Question;

public class SqlRequester {

	public static void insertQuestion(Connection db, Question question) throws SQLException {
		System.out.println(question.id);
		
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
		stat.setInt(idx++, question.asked_to.id);
		stat.setInt(idx++, question.id);
		
		stat.execute();
		stat.close();
	}
}
