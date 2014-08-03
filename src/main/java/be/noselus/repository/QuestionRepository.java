package be.noselus.repository;

import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Question;
import com.google.common.base.Optional;

import java.util.List;

/**
 * Repository allowing access to all the questions available.
 */
public interface QuestionRepository {

    /**
     * Returns the question with the given id.
     *
     * @param id the id of the question to look for.
     */
    Question getQuestionById(int id);

    PartialResult<Question> getQuestions(SearchParameter parameter, Optional<Integer> askedById, String... keywords);

    PartialResult<Question> questionAssociatedToEurovoc(SearchParameter parameter, int id);

    void insertOrUpdateQuestion(Question question);

    /**
     * Returns the {@code assemblyRef} of the question ;ost recently added in the database for the given assembly.
     */
    Integer getMostRecentQuestionFrom(Integer assemblyId);

    /**
     * Returns the list of {@code assemblyRef} of the questions that do not have an answer in the database yet for the given assembly.
     */
    List<Integer> getUnansweredQuestionsFrom(Integer assemblyId);
}
