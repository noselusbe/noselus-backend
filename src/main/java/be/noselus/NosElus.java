package be.noselus;

import be.noselus.model.Person;
import be.noselus.model.PersonSmall;
import be.noselus.model.Question;
import be.noselus.pictures.PictureManager;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.QuestionRepository;
import be.noselus.search.SolrHelper;
import be.noselus.service.JsonTransformer;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spark.Spark.*;

public class NosElus {

    public static void main(String[] args) throws IOException {

        Injector injector = Guice.createInjector(new NosElusModule());

        final QuestionRepository questionRepository = injector.getInstance(QuestionRepository.class);
        final PoliticianRepository politicianRepository = injector.getInstance(PoliticianRepository.class);
        final PictureManager pictureManager = injector.getInstance(PictureManager.class);

        final String port = System.getenv("PORT");
        if (port != null) {
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
                final String q = request.queryParams("q");
                final String askedBy = request.queryParams("asked_by");
                if (q != null) {
                    final String keywords = q.replace("\"", "").replace(" ", "+");
                    
                    SolrQuery parameters = new SolrQuery();
                    parameters.set("q", "text:" + keywords);
                    
                    try {
						QueryResponse resp = SolrHelper.getSolrServer().query(parameters);
						
						SolrDocumentList list = resp.getResults();
						
						List<Question> result = new ArrayList<Question>();
						
						Iterator<SolrDocument> i = list.iterator();
						
						while (i.hasNext()) {
							String uriString = (String) i.next().getFieldValue("id");
							//until now, we just query into question table, with id field
							String[] uriDecomposed = uriString.split(":");
							Integer id = Integer.valueOf(uriDecomposed[uriDecomposed.length - 1]);
							result.add(questionRepository.getQuestionById(id));
						}
						
						return result;
						
						
					} catch (SolrServerException e) {
						e.printStackTrace();
					}
                    
                } else if (askedBy != null) {
                    return questionRepository.questionAskedBy(Integer.valueOf(askedBy));
                }
                return questionRepository.getQuestions(50, 0);
            }
        });

        get(new JsonTransformer("/questions/:id", "question") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return questionRepository.getQuestionById(Integer.parseInt(params));
            }
        });

        get(new JsonTransformer("/questions/askedBy/:name", "questions") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":name");
                List<PersonSmall> list = politicianRepository.getPoliticianByName(params);
                if (list.size() > 0) {
                    return questionRepository.questionAskedBy(list.get(0).id);
                } else {
                    return null;
                }

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


        get(new Route("/politicians/picture/:id/*/*") {
            @Override
            public Object handle(final Request request, final Response response) {
                try {
                    final String id = request.params(":id");
                    int width = Integer.valueOf(request.splat()[0]);
                    int height = Integer.valueOf(request.splat()[1]);

                    response.raw().setContentType("image/jpeg;charset=utf-8");
                    pictureManager.get(Integer.valueOf(id), width, height, response.raw().getOutputStream());
                    return null;
                } catch (NumberFormatException | IOException e) {
                    response.status(404);
                    return null;
                }
            }
        });
        
        
        get(new JsonTransformer("/questions/byEurovoc/:id", "questions") {
			
			@Override
			protected Object myHandle(Request request, Response response) {
				final String id = request.params(":id");
				return questionRepository.questionAssociatedToEurovoc(Integer.valueOf(id));
			}
        	
        });
        
    }
}
