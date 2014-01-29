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

    PartialResult<Question> questionAskedBy(SearchParameter parameter, int askedById);

    PartialResult<Question> getQuestions(SearchParameter parameter, Optional<Integer> askedById, String... keywords);

    List<Question> questionAssociatedToEurovoc(int id);

    void insertOrUpdateQuestion(Question question);

}
