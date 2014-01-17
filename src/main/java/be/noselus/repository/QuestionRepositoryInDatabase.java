package be.noselus.repository;

import be.noselus.db.DatabaseHelper;
import be.noselus.dto.PartialResult;
import be.noselus.model.Eurovoc;
import be.noselus.model.Question;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Singleton
public class QuestionRepositoryInDatabase extends AbstractRepositoryInDatabase implements QuestionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionRepositoryInDatabase.class);

    private final AssemblyRegistry assemblyRegistry;
    private final QuestionMapper mapper;

    @Inject
    public QuestionRepositoryInDatabase(final AssemblyRegistry assemblyRegistry, final DatabaseHelper dbHelper) {
        super(dbHelper);
        this.assemblyRegistry = assemblyRegistry;
        this.mapper = new QuestionMapper(this.assemblyRegistry);
    }

    @Override
    public List<Question> getQuestions() {
        final PartialResult<Question> questions = getQuestions(50);
        return questions.getResults();
    }

    @Override
    public Question getQuestionById(final int id) {
        Question result = null;
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement stat = db.prepareStatement("SELECT * FROM written_question WHERE id = ?;")) {

            stat.setInt(1, id);
            stat.execute();
            stat.getResultSet().next();

            result = mapper.map(stat.getResultSet());

            this.addEurovocsToQuestion(result, db);

        } catch (SQLException e) {
            LOGGER.error("Error loading question with id " + id, e);
        }

        return result;
    }

    @Override
    public List<Question> searchByKeyword(final String... keywords) {
        List<Question> results = Lists.newArrayList();
        final StringBuilder sql = new StringBuilder("SELECT * FROM written_question WHERE lower(title) LIKE ");
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];
            sql.append("lower('%");
            sql.append(keyword);
            sql.append("%')");
            if (i < keywords.length - 1) {
                sql.append(" OR lower(title) LIKE  ");
            }
        }
        sql.append(";");
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement stat = db.prepareStatement(sql.toString())) {

            stat.execute();

            while (stat.getResultSet().next()) {
                final Question question = mapper.map(stat.getResultSet());
                this.addEurovocsToQuestion(question, db);
                results.add(question);
            }

        } catch (SQLException e) {
            LOGGER.error("Error loading question with keywords " + Arrays.toString(keywords), e);
        }
        return results;
    }

    @Override
    public List<Integer> questionIndexAskedBy(final int askedById) {
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionsStat = db.prepareStatement("SELECT id FROM written_question WHERE asked_by = ?;")) {

            questionsStat.setInt(1, askedById);

            questionsStat.execute();
            List<Integer> questionsAskedBy = Lists.newArrayList();
            while (questionsStat.getResultSet().next()) {
                questionsAskedBy.add(questionsStat.getResultSet().getInt("id"));
            }
            return questionsAskedBy;
        } catch (SQLException e) {
            LOGGER.error("Error loading questions asked by " + askedById, e);
        }
        return Collections.emptyList();
    }


    private void addEurovocsToQuestion(Question q, Connection db) {
        try (PreparedStatement stat = db.prepareStatement("SELECT label, id FROM eurovoc JOIN written_question_eurovoc "
                + "ON written_question_eurovoc.id_eurovoc = eurovoc.id "
                + "WHERE written_question_eurovoc.id_written_question = ? ")) {

            stat.setInt(1, q.id);

            stat.execute();

            while (stat.getResultSet().next()) {
                String label = stat.getResultSet().getString("label");
                Integer eurovocId = stat.getResultSet().getInt("id");

                Eurovoc eurovoc = new Eurovoc(eurovocId, label);

                q.addEurovoc(eurovoc);
            }
        } catch (SQLException e) {
            LOGGER.error("ERROR while loading eurovocs from question " + q.id.toString(), e);
        }
    }


    @Override
    public List<Question> questionAskedBy(int askedById) {
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionsStat = db.prepareStatement("SELECT * FROM written_question WHERE asked_by = ? ORDER BY date_asked DESC LIMIT 50;")) {

            questionsStat.setInt(1, askedById);

            questionsStat.execute();
            List<Question> questionsAskedBy = Lists.newArrayList();

            while (questionsStat.getResultSet().next()) {
                Question q = mapper.map(questionsStat.getResultSet());
                this.addEurovocsToQuestion(q, db);
                questionsAskedBy.add(q);
            }
            return questionsAskedBy;
        } catch (SQLException e) {
            LOGGER.error("Error loading questions asked by " + askedById, e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Question> questionAssociatedToEurovoc(int eurovocId) {
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionsStat = db.prepareStatement(""
                     + "SELECT * FROM written_question "
                     + "JOIN written_question_eurovoc "
                     + "ON written_question_eurovoc.id_written_question = written_question.id "
                     + "WHERE written_question_eurovoc.id_eurovoc = ? ")) {

            questionsStat.setInt(1, eurovocId);
            questionsStat.execute();

            List<Question> questionAssociatedToEurovoc = Lists.newArrayList();

            while (questionsStat.getResultSet().next()) {
                Question q = mapper.map(questionsStat.getResultSet());
                this.addEurovocsToQuestion(q, db);
                questionAssociatedToEurovoc.add(q);
            }

            questionsStat.close();

            return questionAssociatedToEurovoc;

        } catch (SQLException e) {
            LOGGER.error("Error loading questions asked by " + eurovocId, e);
        }
        return Collections.emptyList();
    }

    @Override
    public void insertOrUpdateQuestion(final Question question) {
        try (Connection db = dbHelper.getConnection(false, true)) {

            if (questionIsPresent(db, question)) {
                updateQuestion(db, question);
            } else {
                insertQuestion(db, question);
            }
            db.commit();
            db.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartialResult<Question> getQuestions(final Integer limit) {
        List<Question> results = Lists.newArrayList();
        Map<Integer, Question> tempQuestionMapper = new TreeMap<>();
        Map<Integer, Eurovoc> eurovocMappers = new TreeMap<>();
        int total = 0;

        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionCount = db.prepareStatement("SELECT count(*) as total FROM written_question;");
             PreparedStatement questionsStatement = db.prepareStatement("SELECT * FROM written_question ORDER BY date_asked DESC LIMIT ?;");
             PreparedStatement eurovocs = db.prepareStatement("SELECT "
                     + "written_question_eurovoc.id_written_question AS written_question_id, "
                     + "eurovoc.label AS eurovoc_label, "
                     + "eurovoc.id AS eurovoc_id "
                     + "FROM written_question_eurovoc "
                     + "JOIN eurovoc ON written_question_eurovoc.id_eurovoc = eurovoc.id"
                     + "")){

            questionCount.execute();
            questionCount.getResultSet().next();
            total = questionCount.getResultSet().getInt("total");

            questionsStatement.setInt(1,limit+1);
            questionsStatement.execute();

            while (questionsStatement.getResultSet().next()) {
                final Question question = mapper.map(questionsStatement.getResultSet());
                results.add(question);
                tempQuestionMapper.put(question.id, question);
            }

            eurovocs.execute();

            while (eurovocs.getResultSet().next()) {
                Integer writtenQuestionId = eurovocs.getResultSet().getInt("written_question_id");
                String eurovocLabel = eurovocs.getResultSet().getString("eurovoc_label");
                Integer eurovocId = eurovocs.getResultSet().getInt("eurovoc_id");

                Eurovoc eurovoc;

                if (eurovocMappers.get(eurovocId) == null) {
                    eurovoc = new Eurovoc(eurovocId, eurovocLabel);
                    eurovocMappers.put(eurovoc.id, eurovoc);
                } else {
                    eurovoc = eurovocMappers.get(eurovocId);
                }

                if (tempQuestionMapper.get(writtenQuestionId) != null) {
                    Question q = tempQuestionMapper.get(writtenQuestionId);
                    q.addEurovoc(eurovoc);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error loading questions from DB", e);
        }

        final int resultFound = results.size();
        final Integer nextElement;
        if(resultFound > limit){
            nextElement = results.get(resultFound-1).id;
            results.remove(resultFound-1);
        } else {
            nextElement = null;
        }
        return new PartialResult<>(results,nextElement,limit,total);
    }

    @Override
    public PartialResult<Question> getQuestions(final Integer limit, final Integer firstItem) {
        List<Question> results = Lists.newArrayList();
        Map<Integer, Question> tempQuestionMapper = new TreeMap<>();
        Map<Integer, Eurovoc> eurovocMappers = new TreeMap<>();
        int total = 0;

        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionCount = db.prepareStatement("SELECT count(*) as total FROM written_question;");
             PreparedStatement questionsStatement = db.prepareStatement("SELECT * FROM written_question WHERE id < ? ORDER BY date_asked DESC, title ASC LIMIT ?;");
             PreparedStatement eurovocs = db.prepareStatement("SELECT "
                     + "written_question_eurovoc.id_written_question AS written_question_id, "
                     + "eurovoc.label AS eurovoc_label, "
                     + "eurovoc.id AS eurovoc_id "
                     + "FROM written_question_eurovoc "
                     + "JOIN eurovoc ON written_question_eurovoc.id_eurovoc = eurovoc.id"
                     + "")){

            questionCount.execute();
            questionCount.getResultSet().next();
            total = questionCount.getResultSet().getInt("total");

            questionsStatement.setInt(1,firstItem);
            questionsStatement.setInt(2,limit+1);
            questionsStatement.execute();

            while (questionsStatement.getResultSet().next()) {
                final Question question = mapper.map(questionsStatement.getResultSet());
                results.add(question);
                tempQuestionMapper.put(question.id, question);
            }

            eurovocs.execute();

            while (eurovocs.getResultSet().next()) {
                Integer writtenQuestionId = eurovocs.getResultSet().getInt("written_question_id");
                String eurovocLabel = eurovocs.getResultSet().getString("eurovoc_label");
                Integer eurovocId = eurovocs.getResultSet().getInt("eurovoc_id");

                Eurovoc eurovoc;

                if (eurovocMappers.get(eurovocId) == null) {
                    eurovoc = new Eurovoc(eurovocId, eurovocLabel);
                    eurovocMappers.put(eurovoc.id, eurovoc);
                } else {
                    eurovoc = eurovocMappers.get(eurovocId);
                }

                if (tempQuestionMapper.get(writtenQuestionId) != null) {
                    Question q = tempQuestionMapper.get(writtenQuestionId);
                    q.addEurovoc(eurovoc);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error loading questions from DB", e);
        }

        final int resultFound = results.size();
        final Integer nextElement;
        if(resultFound > limit){
            nextElement = results.get(resultFound-1).id;
            results.remove(resultFound-1);
        } else {
            nextElement = null;
        }
        return new PartialResult<>(results,nextElement,limit,total);
    }


    private void insertQuestion(final Connection db, final Question question) throws SQLException {
        LOGGER.debug("Inserting question " + question.assembly.getLabel() + " " + question.assemblyRef);

        String sql =
                "INSERT INTO written_question (session, year, number, date_asked, date_answer, title, question_text, answer_text, asked_by, asked_to, assembly_ref, assembly_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stat = db.prepareStatement(sql)) {
            int idx = 1;
            stat.setString(idx++, question.session);
            stat.setInt(idx++, question.year);
            stat.setString(idx++, question.number);
            stat.setDate(idx++, new java.sql.Date(question.dateAsked.toDate().getTime()));
            if (question.dateAnswered == null) {
                stat.setNull(idx++, java.sql.Types.DATE);
            } else {
                stat.setDate(idx++, new java.sql.Date(question.dateAnswered.toDate().getTime()));
            }
            stat.setString(idx++, question.title);
            stat.setString(idx++, question.questionText);
            stat.setString(idx++, question.answerText);
            stat.setInt(idx++, question.askedBy);
            stat.setInt(idx++, question.askedTo);
            stat.setString(idx++, question.assemblyRef);
            stat.setInt(idx++, question.assembly.getId());

            stat.execute();
        }
    }

    private void updateQuestion(final Connection db, final Question question) throws SQLException {
        LOGGER.debug("Updating question " + question.assembly.getLabel() + " " + question.assemblyRef);

        String sql =
                "UPDATE written_question SET session = ?,  year = ? , number = ?, date_asked = ?, date_answer = ?, title = ?, question_text = ?, answer_text = ?, asked_by = ?, asked_to = ? "
                        + "WHERE assembly_ref = ? AND assembly_id = ?";
        try (PreparedStatement stat = db.prepareStatement(sql)) {
            int idx = 1;
            stat.setString(idx++, question.session);
            stat.setInt(idx++, question.year);
            stat.setString(idx++, question.number);
            stat.setDate(idx++, new java.sql.Date(question.dateAsked.toDate().getTime()));
            if (question.dateAnswered == null) {
                stat.setNull(idx++, java.sql.Types.DATE);
            } else {
                stat.setDate(idx++, new java.sql.Date(question.dateAnswered.toDate().getTime()));
            }
            stat.setString(idx++, question.title);
            stat.setString(idx++, question.questionText);
            stat.setString(idx++, question.answerText);
            stat.setInt(idx++, question.askedBy);
            stat.setInt(idx++, question.askedTo);
            stat.setString(idx++, question.assemblyRef);
            stat.setInt(idx++, question.assembly.getId());

            stat.execute();
        }
    }

    private boolean questionIsPresent(final Connection db, final Question question) throws SQLException {
        LOGGER.debug("Checking if question is present " + question.assembly.getLabel() + " " + question.assemblyRef);
        String sql = "SELECT id FROM written_question WHERE assembly_ref = ? AND assembly_id = ?";
        try (PreparedStatement stat = db.prepareStatement(sql)) {
            int idx = 1;
            stat.setString(idx++, question.assemblyRef);
            stat.setInt(idx++, question.assembly.getId());
            stat.execute();

            return stat.getResultSet().next();
        }
    }

}
