package be.noselus.repository;

import be.noselus.db.DatabaseHelper;
import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
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
    private static final String SELECT_QUESTION = "SELECT * FROM written_question WHERE 1=1 ";
    public static final String ORDER_BY = " ORDER BY date_asked DESC, id DESC";

    private final QuestionMapper mapper;

    @Inject
    public QuestionRepositoryInDatabase(final AssemblyRegistry assemblyRegistry, final DatabaseHelper dbHelper) {
        super(dbHelper);
        this.mapper = new QuestionMapper(assemblyRegistry);
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
    public PartialResult<Question> searchByKeyword(final SearchParameter parameter, final String... keywords) {
        List<Question> results = Lists.newArrayList();
        final StringBuilder sql = new StringBuilder(SELECT_QUESTION);
        final StringBuilder where = new StringBuilder("AND (lower(title) LIKE ");
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];
            where.append("lower('%");
            where.append(keyword);
            where.append("%')");
            if (i < keywords.length - 1) {
                where.append(" OR lower(title) LIKE  ");
            }
        }
        where.append(")");
        sql.append(where);
        sql.append(ORDER_BY);
        sql.append(" LIMIT ?");
        sql.append(";");
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement stat = db.prepareStatement(sql.toString())) {
            stat.setInt(1, parameter.getLimit()+1);
            stat.execute();

            while (stat.getResultSet().next()) {
                final Question question = mapper.map(stat.getResultSet());
                this.addEurovocsToQuestion(question, db);
                results.add(question);
            }


        } catch (SQLException e) {
            LOGGER.error("Error loading question with keywords " + Arrays.toString(keywords), e);
        }
        return makePartialResult(results, parameter.getLimit(), null);
    }

    @Override
    public List<Integer> questionIndexAskedBy(final int askedById) {
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionsStat = db.prepareStatement("SELECT id FROM written_question WHERE asked_by = ? " + ORDER_BY + ";")) {

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
    public PartialResult<Question> questionAskedBy(final SearchParameter parameter, int askedById) {
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionsStat = db.prepareStatement(SELECT_QUESTION + " AND asked_by = ? "+ ORDER_BY + " LIMIT ?;")) {

            questionsStat.setInt(1, askedById);
            questionsStat.setInt(2, parameter.getLimit());

            questionsStat.execute();
            List<Question> questionsAskedBy = Lists.newArrayList();

            while (questionsStat.getResultSet().next()) {
                Question q = mapper.map(questionsStat.getResultSet());
                this.addEurovocsToQuestion(q, db);
                questionsAskedBy.add(q);
            }
            return makePartialResult(questionsAskedBy, parameter.getLimit(), null);
        } catch (SQLException e) {
            LOGGER.error("Error loading questions asked by " + askedById, e);
        }
        final List<Question> objects  = Collections.emptyList();
        return makePartialResult(objects, parameter.getLimit(), null);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartialResult<Question> getQuestions(final SearchParameter parameter) {
        Integer limit = parameter.getLimit();
        List<Question> results = Lists.newArrayList();
        Map<Integer, Question> tempQuestionMapper = new TreeMap<>();
        Map<Integer, Eurovoc> eurovocMappers = new TreeMap<>();
        int total = 0;
        String whereClause = "";
        final Integer firstElement = (Integer) parameter.getFirstElement();
        if (firstElement != null) {
            whereClause += " AND date_asked <= ? AND id <= ?";
        }
        try (Connection db = dbHelper.getConnection(false, true);
             PreparedStatement questionCount = db.prepareStatement("SELECT count(*) AS total FROM written_question;");

             PreparedStatement questionsStatement = db.prepareStatement(SELECT_QUESTION + whereClause + ORDER_BY + " LIMIT ?;");
             PreparedStatement eurovocs = db.prepareStatement("SELECT "
                     + "written_question_eurovoc.id_written_question AS written_question_id, "
                     + "eurovoc.label AS eurovoc_label, "
                     + "eurovoc.id AS eurovoc_id "
                     + "FROM written_question_eurovoc "
                     + "JOIN eurovoc ON written_question_eurovoc.id_eurovoc = eurovoc.id"
                     + "")) {

            questionCount.execute();
            questionCount.getResultSet().next();
            total = questionCount.getResultSet().getInt("total");

            int parameterPosition = 1;
            if (firstElement != null) {
                final Question firstQuestion = getQuestionById(firstElement);
                if (firstQuestion == null){
                    throw new RuntimeException("No question with id " + firstElement);
                }
                questionsStatement.setDate(parameterPosition++, new java.sql.Date(firstQuestion.dateAsked.toDate().getTime()));
                questionsStatement.setInt(parameterPosition++, firstQuestion.id);
            }
            questionsStatement.setInt(parameterPosition, limit + 1);
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

        final PartialResult<Question> questionPartialResult = makePartialResult(results, limit, total);
        return questionPartialResult;
    }

    private PartialResult<Question> makePartialResult(final List<Question> results, final Integer limit, final Integer total) {
        final int resultFound = results.size();
        final Integer nextElement;
        if (resultFound > limit) {
            nextElement = results.get(resultFound - 1).id;
            results.remove(resultFound - 1);
        } else {
            nextElement = null;
        }
        return new PartialResult<>(results, nextElement, limit, total);
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
