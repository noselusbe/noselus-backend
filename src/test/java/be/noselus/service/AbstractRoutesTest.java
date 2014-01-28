package be.noselus.service;

import be.noselus.NosElus;
import be.noselus.NosElusModule;
import be.noselus.NosElusTestModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.jayway.restassured.RestAssured;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import spark.Spark;

public abstract class AbstractRoutesTest {

    protected static NosElus server;
    protected static Injector injector;

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            if (server == null){
                Spark.setPort(4566);
                AbstractRoutesTest.injector = Guice.createInjector(Modules.override(new NosElusModule()).with(new NosElusTestModule()));
                server = injector.getInstance(NosElus.class);
                RestAssured.port = 4566;
                server.initialize();
            }
        }

        @Override
        protected void after() {
        }
    };
}
