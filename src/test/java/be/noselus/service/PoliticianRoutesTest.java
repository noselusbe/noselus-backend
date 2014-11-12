package be.noselus.service;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;

public class PoliticianRoutesTest extends AbstractRoutesTest {

    @Test
    public void returnsAllPoliticians(){
        expect().statusCode(200)
                .body("politicians.size()", equalTo(293))
                .when()
                .get("/politicians");
    }

    @Test
    public void returnsAPoliticianById() {
        expect().
                statusCode(200).
                root("politician").
                body("id", equalTo(151),
                        "fullName", equalTo("DEMOTTE Rudy")).
                when().
                get("/politicians/151");
    }

    @Test
    public void returns040WhenAPoliticianDoesNotExist() {
        expect().
                statusCode(404).
                when().
                get("/politicians/10");
    }

    @Test
    public void returnsPoliticianPicture(){
        expect().statusCode(200)
                .contentType("image/jpeg;charset=utf-8")
                .when()
                .get("/politicians/151/picture");
    }

    @Test
    public void returnsPoliticianPictureResized(){
        expect().statusCode(200)
                .contentType("image/jpeg;charset=utf-8")
                .when()
                .get("/politicians/151/picture/150/140");
    }

    @Test
    public void returns404WhenPictureNotPresent(){
        expect().statusCode(404)
                .when()
                .get("/politicians/100000/picture");
    }

    @Test
    public void returns404WhenPictureNotPresentWithCustomSize(){
        expect().statusCode(404)
                .when()
                .get("/politicians/100000/picture/100/100");
    }
}
