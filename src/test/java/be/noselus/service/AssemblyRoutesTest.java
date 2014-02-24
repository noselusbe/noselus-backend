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

    @Test
    public void returnsAssemblyById(){
        expect().statusCode(200)
                .root("assembly")
                .body("id", equalTo(1),
                        "label", equalTo("Parlement Wallon"))
                .when()
                .get("/assemblies/1");
    }
}
