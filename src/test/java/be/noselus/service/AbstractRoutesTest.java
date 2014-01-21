package be.noselus.service;

import com.jayway.restassured.RestAssured;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import spark.Spark;

public abstract class AbstractRoutesTest {
    private static boolean initialized = false;

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            if (!initialized) {
                Spark.setPort(4566);
                RestAssured.port = 4566;
                initialized = true;
            }
        }

        @Override
        protected void after() {
        }
    };
}
