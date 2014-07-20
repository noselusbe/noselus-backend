package be.noselus.service;

import be.noselus.model.Assembly;
import be.noselus.repository.AssemblyRepository;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static spark.Spark.get;

@Singleton
public class AssembliesRoutes implements Routes {

    private final RoutesHelper helper;
    private final AssemblyRepository assemblyRepository;

    @Inject
    public AssembliesRoutes(final RoutesHelper helper, final AssemblyRepository assemblyRepository) {
        this.helper = helper;
        this.assemblyRepository = assemblyRepository;
    }

    @Override
    public void setup() {
        get(new JsonTransformer("/assemblies") {
            @Override
            protected Object myHandle(final Request request, final Response response) {
                final List<Assembly> assemblies = assemblyRepository.getAssemblies();
                return helper.resultAs("assemblies", assemblies);
            }
        });

        get(new JsonTransformer("/assemblies/:id") {
            @Override
            protected Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                Integer assemblyId = Integer.parseInt(params);
                return helper.resultAs("assembly", assemblyRepository.findId(assemblyId));
            }
        });
    }
}
