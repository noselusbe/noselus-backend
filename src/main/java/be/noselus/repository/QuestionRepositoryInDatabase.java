package be.noselus.repository;

import be.noselus.db.DatabaseHelper;
import be.noselus.model.Question;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class QuestionRepositoryInDatabase implements QuestionRepository {

    private static final Logger logger = LoggerFactory.getLogger(QuestionRepositoryInDatabase.class);


    private QuestionMapper mapper = new QuestionMapper();

    @Override
    public List<Question> getQuestions() {
        List<Question> results = Lists.newArrayList();
        try {
            Connection db = DatabaseHelper.openConnection(false, true);
            PreparedStatement stat = db.prepareStatement("SELECT * FROM written_question LIMIT 50;");

            stat.execute();

            while (stat.getResultSet().next()) {
                final Question question = mapper.map(stat.getResultSet());
                results.add(question);
            }

            stat.close();

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error loading person from DB", e);
        }
        return results;
    }

    @Override
    public Question getQuestionById(final int id) {
        Question result = null;
        try {
            Connection db = DatabaseHelper.openConnection(false, true);
            PreparedStatement stat = db.prepareStatement("SELECT * FROM written_question WHERE id = ?;");

            stat.setInt(1, id);
            stat.execute();
            stat.getResultSet().next();

            result = mapper.map(stat.getResultSet());

            stat.close();

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error loading person from DB", e);
        }

        return result;
    }

    @Override
    public List<Question> searchByKeyword(final String... keywords) {
        List<Question> results = Lists.newArrayList();
        try {
            Connection db = DatabaseHelper.openConnection(false, true);
            final StringBuffer sql = new StringBuffer("SELECT * FROM written_question WHERE title LIKE ");
            for (int i = 0; i < keywords.length; i++) {
                String keyword = keywords[i];
                sql.append("'%");
                sql.append(keyword);
                sql.append("%'");
                if (i < keywords.length - 1){
                    sql.append(" OR title LIKE  ");
                }

            }
            sql.append(";");
            PreparedStatement stat = db.prepareStatement(sql.toString());

            stat.execute();

            while (stat.getResultSet().next()) {
                final Question question = mapper.map(stat.getResultSet());
                results.add(question);
            }

            stat.close();

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error loading person from DB", e);
        }
        return results;
    }
}
