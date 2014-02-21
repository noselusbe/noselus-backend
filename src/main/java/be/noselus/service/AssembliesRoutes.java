package be.noselus.service;

import be.noselus.model.Assembly;
import be.noselus.repository.AssemblyRegistry;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import static spark.Spark.get;

@Singleton
public class AssembliesRoutes implements Routes {

    private final RoutesHelper helper;
    private final AssemblyRegistry assemblyRegistry;

    @Inject
    public AssembliesRoutes(final RoutesHelper helper, final AssemblyRegistry assemblyRegistry) {
        this.helper = helper;
        this.assemblyRegistry = assemblyRegistry;
    }

    @Override
    public void setup() {
        get(new JsonTransformer("/assemblies") {
            @Override
            protected Object myHandle(final Request request, final Response response) {
                final List<Assembly> assemblies = assemblyRegistry.getAssemblies();
                return helper.resultAs("assemblies", assemblies);
            }
        });
    }
}
