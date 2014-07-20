package be.noselus.repository;

import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Eurovoc;
import be.noselus.model.Question;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class QuestionRepositoryInDatabase extends AbstractRepositoryInDatabase implements QuestionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionRepositoryInDatabase.class);
    private static final String SELECT_QUESTION = "SELECT * FROM written_question WHERE 1=1 ";
    public static final String ORDER_BY = " ORDER BY date_asked DESC, id DESC";
    public static final String NEXT_ELEMENT_WHERE = " AND date_asked <= ? AND id <= ?";
    public static final String LIMIT = " LIMIT ?";
    public static final String OFFSET = " OFFSET ?;";

    private final QuestionMapper mapper;

    @Inject
    public QuestionRepositoryInDatabase(final AssemblyRepository assemblyRepository, final DataSource dataSource) {
        super(dataSource);
        this.mapper = new QuestionMapper(assemblyRepository);
    }

    @Override
    public Question getQuestionById(final int id) {
        Question result = null;
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement("SELECT * FROM written_question WHERE id = ?;")) {

            stat.setInt(1, id);
            stat.execute();
            stat.getResultSet().next();

            result = mapper.map(stat.getResultSet());

            this.addEurovocsToQuestion(result, db);

        } catch (SQLException e) {
            LOGGER.error("Error loading question with id {}", id, e);
        }

        return result;
    }

    @Override
    public void insertOrUpdateQuestion(final Question question) {
        try (Connection db = dataSource.getConnection()) {

            if (questionIsPresent(db, question)) {
                updateQuestion(db, question);
            } else {
                insertQuestion(db, question);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getMostRecentQuestionFrom(final Integer assemblyId) {
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement("SELECT max(assembly_ref) FROM written_question WHERE assembly_id = ?;")) {

            stat.setInt(1, assemblyId);
            stat.execute();
            stat.getResultSet().next();
            return stat.getResultSet().getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getUnansweredQuestionsFrom(final Integer assemblyId) {
        List<Integer> result = new ArrayList<>();
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement("SELECT assembly_ref FROM written_question WHERE assembly_id = ? AND date_answer IS NULL;")) {
            stat.setInt(1, assemblyId);
            stat.execute();
            while (stat.getResultSet().next()){
                result.add(stat.getResultSet().getInt("assembly_ref"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Timed
    @Override
    public PartialResult<Question> getQuestions(final SearchParameter parameter, final Optional<Integer> askedById, final String... keywords) {
        List<Question> results = Lists.newArrayList();
        int totalResults = 0;
        final StringBuilder sql = new StringBuilder(SELECT_QUESTION);
        final StringBuilder where = new StringBuilder();
        addKeywordsClause(where, keywords);
        addAskedByClause(askedById, where);
        sql.append(where);
        sql.append(ORDER_BY);
        sql.append(LIMIT);
        sql.append(OFFSET);
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement(sql.toString());
             PreparedStatement countStat = db.prepareStatement("SELECT COUNT(*) FROM WRITTEN_QUESTION WHERE 1=1 " + where)) {
            int position = 1;
            position = addAskedByParameter(askedById, position, stat, countStat);
            stat.setInt(position++, parameter.getLimit() + 1);
            stat.setInt(position, parameter.getFirstElement() == null ? 0 : (Integer) parameter.getFirstElement());
            stat.execute();

            while (stat.getResultSet().next()) {
                final Question question = mapper.map(stat.getResultSet());
                this.addEurovocsToQuestion(question, db);
                results.add(question);
            }

            countStat.execute();
            countStat.getResultSet().next();
            totalResults = countStat.getResultSet().getInt(1);

        } catch (SQLException e) {
            LOGGER.error("Error loading question with keywords " + Arrays.toString(keywords), e);
        }
        return makePartialResult(results, parameter, totalResults);
    }

    @Override
    public PartialResult<Question> questionAssociatedToEurovoc(SearchParameter parameter, int eurovocId) {
        List<Question> questionAssociatedToEurovoc = Lists.newArrayList();
        final StringBuilder sql = new StringBuilder("SELECT * FROM written_question "
                + "JOIN written_question_eurovoc "
                + "ON written_question_eurovoc.id_written_question = written_question.id "
                + "WHERE written_question_eurovoc.id_eurovoc = ? ");
        final String count = "SELECT count(*) FROM written_question "
                + "JOIN written_question_eurovoc "
                + "ON written_question_eurovoc.id_written_question = written_question.id "
                + "WHERE written_question_eurovoc.id_eurovoc = ? ";

        final Integer firstElement = addClauseForNext(parameter, sql);
        sql.append(ORDER_BY);
        sql.append(LIMIT);

        int total = 0;
        try (Connection db = dataSource.getConnection();
             PreparedStatement questionsStat = db.prepareStatement(
                     sql.toString());
             PreparedStatement countStatement = db.prepareCall(count)) {
            int position = 1;
            questionsStat.setInt(position, eurovocId);
            countStatement.setInt(position, eurovocId);
            position++;
            position = addParameterForNext(firstElement, position, questionsStat);
            questionsStat.setInt(position, parameter.getLimit() + 1);
            questionsStat.execute();

            while (questionsStat.getResultSet().next()) {
                Question q = mapper.map(questionsStat.getResultSet());
                this.addEurovocsToQuestion(q, db);
                questionAssociatedToEurovoc.add(q);
            }
            countStatement.execute();
            countStatement.getResultSet().next();
            total = countStatement.getResultSet().getInt(1);
        } catch (SQLException e) {
            LOGGER.error("Error loading questions asked by " + eurovocId, e);
        }
        return makePartialResult(questionAssociatedToEurovoc, parameter, total);
    }

    @Timed
    public List<Question> getQuestionsByIds(final List<Integer> ids) {
        List<Question> result = new ArrayList<>(ids.size());
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement("SELECT * FROM written_question WHERE id = ANY ( ? ) ORDER BY date_asked DESC, id DESC;")) {
            final Array anInt = db.createArrayOf("int", ids.toArray());
            stat.setArray(1, anInt);
            stat.execute();
            while (stat.getResultSet().next()) {
                final Question question = mapper.map(stat.getResultSet());
                addEurovocsToQuestion(question, db);
                result.add(question);
            }
        } catch (SQLException e) {
            LOGGER.error("Error loading question with ids {}", ids, e);
            throw new RuntimeException(e);
        }
        return result;
    }

    private Integer addClauseForNext(final SearchParameter parameter, final StringBuilder where) {
        final Integer firstElement = (Integer) parameter.getFirstElement();
        if (firstElement != null) {
            where.append(NEXT_ELEMENT_WHERE);
        }
        return firstElement;
    }

    private int addAskedByParameter(final Optional<Integer> askedById, int position, final PreparedStatement... stats) throws SQLException {
        if (askedById.isPresent()) {
            for (PreparedStatement stat : stats) {
                stat.setInt(position, askedById.get());
            }
            position++;
        }
        return position;
    }

    private void addAskedByClause(final Optional<Integer> askedById, final StringBuilder where) {
        if (askedById.isPresent()) {
            where.append(" AND asked_by = ? ");
        }
    }

    private void addKeywordsClause(final StringBuilder where, final String[] keywords) {
        if (keywords.length > 0) {
            where.append("AND (lower(title) LIKE ");
        }
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];
            where.append("lower('%");
            where.append(keyword);
            where.append("%')");
            if (i < keywords.length - 1) {
                where.append(" OR lower(title) LIKE  ");
            }
        }
        if (keywords.length > 0) {
            where.append(")");
        }
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

    private int addParameterForNext(final Integer firstElement, int parameterPosition, final PreparedStatement... statements) throws SQLException {
        if (firstElement != null) {
            final Question firstQuestion = getQuestionById(firstElement);
            if (firstQuestion == null) {
                throw new RuntimeException("No question with id " + firstElement);
            }
            for (PreparedStatement statement : statements) {
                statement.setDate(parameterPosition, new java.sql.Date(firstQuestion.dateAsked.toDate().getTime()));
            }
            parameterPosition++;
            for (PreparedStatement statement : statements) {
                statement.setInt(parameterPosition, firstQuestion.id);
            }
            parameterPosition++;
        }
        return parameterPosition;
    }

    protected PartialResult<Question> makePartialResult(final List<Question> results, final SearchParameter parameter, final long total) {
        final int resultFound = results.size();
        final Integer nextElement;
        final int limit = parameter.getLimit();
        if (resultFound > limit) {
            final Integer firstElement = (Integer) parameter.getFirstElement();
            nextElement = firstElement == null ? limit : firstElement + limit;
            results.remove(resultFound - 1);
        } else {
            nextElement = null;
        }
        return new PartialResult<>(results, nextElement, limit, total);
    }

    private void insertQuestion(final Connection db, final Question question) throws SQLException {
        LOGGER.debug("Inserting question " + question.assembly.getLabel() + " " + question.assemblyRef);

        String sql =
                "INSERT INTO written_question (session, year, number, date_asked, date_answer, title, question_text, answer_text, asked_by, asked_to, assembly_ref, assembly_id, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            stat.setTimestamp(idx, new Timestamp(new LocalDateTime().toDate().getTime()));
            stat.execute();
        }
    }

    private void updateQuestion(final Connection db, final Question question) throws SQLException {
        LOGGER.debug("Updating question " + question.assembly.getLabel() + " " + question.assemblyRef);

        String sql =
                "UPDATE written_question SET session = ?,  year = ? , number = ?, date_asked = ?, date_answer = ?, title = ?, question_text = ?, answer_text = ?, asked_by = ?, asked_to = ?, updated_at = ? "
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
            stat.setTimestamp(idx++, new Timestamp(new LocalDateTime().toDate().getTime()));

            //WHERE clause
            stat.setString(idx++, question.assemblyRef);
            stat.setInt(idx, question.assembly.getId());

            stat.execute();
        }
    }

    private boolean questionIsPresent(final Connection db, final Question question) throws SQLException {
        LOGGER.debug("Checking if question is present " + question.assembly.getLabel() + " " + question.assemblyRef);
        String sql = "SELECT id FROM written_question WHERE assembly_ref = ? AND assembly_id = ?";
        try (PreparedStatement stat = db.prepareStatement(sql)) {
            int idx = 1;
            stat.setString(idx++, question.assemblyRef);
            stat.setInt(idx, question.assembly.getId());
            stat.execute();

            return stat.getResultSet().next();
        }
    }

}
