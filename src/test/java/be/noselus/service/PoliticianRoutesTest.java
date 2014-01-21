package be.noselus.service;

import be.noselus.model.Assembly;
import be.noselus.model.Person;
import be.noselus.model.PersonFunction;
import be.noselus.pictures.PictureManager;
import be.noselus.repository.PoliticianRepository;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class PoliticianRoutesTest {

    private PictureManager pictureManager = mock(PictureManager.class);
    private PoliticianRepository politicianRepository = mock(PoliticianRepository.class);

    @Before
    public void setup() {
        PoliticianRoutes routes = new PoliticianRoutes(pictureManager, politicianRepository, new RoutesHelper());
        routes.setup();
        RestAssured.port = 4567;
        given(politicianRepository.getPoliticianById(1)).willReturn(new Person(1, "name", "party", "address",
                "postalcode", "town", "phone", "fax", "email", "site", PersonFunction.DEPUTY, 1,
                Collections.<Integer>emptyList(), new Assembly(1, "label", Assembly.Level.DEPUTY_CHAMBER), 0, 0));
    }

    @Test
    public void gettingAPoliticianById() {
        expect().
                statusCode(200).
                root("politician").
                body("id", equalTo(1),
                        "fullName", equalTo("name")).
                when().
                get("/politicians/1");
    }
    @Test
    public void gettingAPoliticianThatDoesNotExist() {
        expect().
                statusCode(404).
                when().
                get("/politicians/10");
    }
}
