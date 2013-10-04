package be.noselus;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

import static spark.Spark.get;

public class NosElus {


    public static void main(String[] args) throws IOException {

        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "<html><head></head><body>Hello World!</body></html>";
            }
        });

    }
}
