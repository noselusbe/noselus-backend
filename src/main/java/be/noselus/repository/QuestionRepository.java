package be.noselus.repository;

import java.util.List;

import be.noselus.model.Question;

public interface QuestionRepository {

    public List<Question> getQuestions();
    
    Question getQuestionById(int id);
}
