package be.noselus.service;

import be.noselus.model.Assembly;
import be.noselus.repository.AssemblyRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static be.noselus.service.RoutesHelper.getJson;

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
        getJson("/assemblies",  (request, response) -> {
            final List<Assembly> assemblies = assemblyRepository.getAssemblies();
            return helper.resultAs("assemblies", assemblies);
        });


        getJson("/assemblies/:id", (request, response) -> {
            final String params = request.params(":id");
            Integer assemblyId = Integer.parseInt(params);
            return helper.resultAs("assembly", assemblyRepository.findId(assemblyId));
        });
    }
}
