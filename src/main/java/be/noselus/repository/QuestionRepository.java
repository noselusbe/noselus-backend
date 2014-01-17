package be.noselus.repository;

import be.noselus.dto.PartialResult;
import be.noselus.model.Question;

import java.util.List;

/**
 * Repository allowing access to all the questions available.
 */
public interface QuestionRepository {

    /**
     * Returns the 50 most recently asked questions.
     */
    List<Question> getQuestions();

    /**
     * Returns the question with the given id.
     *
     * @param id the id of the question to look for.
     */
    Question getQuestionById(int id);

    List<Question> searchByKeyword(String... keywords);

    List<Integer> questionIndexAskedBy(int askedById);
    
    List<Question> questionAskedBy(int askedById);
    
    List<Question> questionAssociatedToEurovoc(int id);

    void insertOrUpdateQuestion(Question question);

    PartialResult<Question> getQuestions(Integer limit);

    PartialResult<Question> getQuestions(Integer limit, Integer firstItem);
}
