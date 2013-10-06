package be.noselus.repository;

import be.noselus.model.Question;

import java.util.List;

public interface QuestionRepository {

    public List<Question> getQuestions();
    
    Question getQuestionById(int id);

    List<Question> searchByKeyword(String... keywords);

    List<Integer> questionIndexAskedBy(int askedById);
    
    List<Question> questionAskedBy(int askedById);
}
