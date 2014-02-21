package be.noselus.service;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;

public class AssemblyRoutesTest extends AbstractRoutesTest {

    @Test
    public void returnsAllAssemblies(){
        expect().statusCode(200)
                .body("assemblies.size()", equalTo(7))
                .when()
                .get("/assemblies");
    }
}
