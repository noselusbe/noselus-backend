package be.noselus;

import be.noselus.pictures.PictureManager;
import be.noselus.repository.AssemblyRegistry;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.QuestionRepository;
import be.noselus.service.JsonTransformer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;

import static spark.Spark.*;

public class NosElus {

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new NosElusModule());

        final AssemblyRegistry assemblyRegistry = injector.getInstance(AssemblyRegistry.class);
        final QuestionRepository questionRepository = injector.getInstance(QuestionRepository.class);
        final PoliticianRepository politicianRepository = injector.getInstance(PoliticianRepository.class);
        final PictureManager pictureManager = injector.getInstance(PictureManager.class);

        final String port = System.getenv("PORT");
        if (port != null){
          setPort(Integer.parseInt(port));

        }

        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "<html><head></head><body>Hello World!</body></html>";
            }
        });

        get(new JsonTransformer("/questions", "questions") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                return questionRepository.getQuestions();
            }
        });

        post(new JsonTransformer("/questions/:keywords", "questions") {

            @Override
            protected Object myHandle(final Request request, final Response response) {
                final String keywords = request.params("keywords");
                return questionRepository.searchByKeyword(keywords.split("%20"));
            }
        });

        get(new JsonTransformer("/questions/:id", "question") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return questionRepository.getQuestionById(Integer.parseInt(params));
            }
        });

        get(new JsonTransformer("/politicians", "politicians") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                return politicianRepository.getPoliticians();
            }
        });

        get(new JsonTransformer("/politicians/:id", "politician") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return politicianRepository.getPoliticianById(Integer.parseInt(params));
            }
        });
        
        get(new Route("/politicians/picture/:id") {
        	@Override
        	public Object handle(final Request request, final Response response) {
        		try {
	        		final String id = request.params(":id");
//	        		int width = Integer.valueOf((String)request.attribute("w"));
//	        		int height = Integer.valueOf((String)request.attribute("h"));
	        		byte[] out = null;
	        		InputStream is = pictureManager.get(Integer.valueOf(id));
	        		
	        		if (is == null) {
	        			response.status(404);
	        			return null;
	        		} else {
		        		out = IOUtils.toByteArray(is);
		        		response.raw().setContentType("image/jpeg;charset=utf-8");
		        		response.raw().getOutputStream().write(out, 0, out.length);
						return out;
	        		}
				} catch (NumberFormatException | IOException e) {
					response.status(404);
					return null;
				}
        	}
        });
        
    }
}
