package be.noselus.service;

import be.noselus.dto.SearchParameter;
import be.noselus.model.PersonSmall;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.QuestionRepository;
import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static be.noselus.service.RoutesHelper.getJson;

@Singleton
public class QuestionRoutes implements Routes {

    public static final String QUESTIONS = "questions";
    public static final String QUESTION = "question";
    private final QuestionRepository questionRepository;
    private final PoliticianRepository politicianRepository;
    private final RoutesHelper helper;

    @Inject
    public QuestionRoutes(final QuestionRepository questionRepository, final PoliticianRepository politicianRepository, final RoutesHelper helper) {
        this.questionRepository = questionRepository;
        this.politicianRepository = politicianRepository;
        this.helper = helper;
    }

    @Override
    public void setup() {
        getJson("/questions", (request, response) -> {
            final String q = request.queryParams("q");
            final String askedBy = request.queryParams("asked_by");
            final SearchParameter parameter = helper.getSearchParameter(request);
            Optional<Integer> askedById;
            if (askedBy == null) {
                askedById = Optional.absent();
            } else {
                askedById = Optional.of(Integer.valueOf(askedBy));
            }
            final String[] keywordsArray;
            if (q == null) {
                keywordsArray = new String[0];
            } else {
                final String keywords = q.replace("\"", "");
                keywordsArray = keywords.split(" ");
            }

            return helper.resultAs(QUESTIONS, questionRepository.getQuestions(parameter, askedById, keywordsArray));
        });
        getJson("/questions/:id", (request, response) -> {
            final String params = request.params(":id");
            return helper.resultAs(QUESTION, questionRepository.getQuestionById(Integer.parseInt(params)));
        });
        getJson("/questions/askedBy/:name", (request, response) -> {
            final String params = request.params(":name");
            List<PersonSmall> list = politicianRepository.getPoliticianByName(params);
            if (list.isEmpty()) {
                return null;
            } else {
                return helper.resultAs(QUESTIONS, questionRepository.getQuestions(helper.getSearchParameter(request), Optional.of(list.get(0).id)));
            }
        });
        getJson("/questions/byEurovoc/:id", (request, response) -> {
            final String id = request.params(":id");
            final SearchParameter parameter = helper.getSearchParameter(request);
            return helper.resultAs(QUESTIONS, questionRepository.questionAssociatedToEurovoc(parameter, Integer.valueOf(id)));
        });
    }
}
