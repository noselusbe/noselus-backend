package be.noselus.fix;

import be.noselus.model.AssemblyEnum;
import be.noselus.model.Person;
import be.noselus.repository.PoliticianRepository;
import be.noselus.scraping.WalloonRepresentativeDocument;
import be.noselus.scraping.WalloonRepresentativesFetcher;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class PersonWithMissingAssemblyRefFix {

    private static final Logger LOGGER = getLogger(PersonWithMissingAssemblyRefFix.class);


    private final PoliticianRepository politicianRepository;
    private final WalloonRepresentativesFetcher fetcher;

    @Inject
    public PersonWithMissingAssemblyRefFix(final PoliticianRepository politicianRepository, final WalloonRepresentativesFetcher fetcher) {
        this.politicianRepository = politicianRepository;
        this.fetcher = fetcher;
    }

    public void runFix(){
        final List<Person> politicians = politicianRepository.getPoliticians();
        politicians.stream().filter((Person p) -> personMissingIdInAssembly(p))
                .forEach(person -> {
                    try {
                        final List<WalloonRepresentativeDocument> documents = fetcher.searchFor(person.fullName);
                        if (documents.size() == 1) {
                            fillData(person, documents.get(0));
                        } else {
                            reportProblem(person, documents);
                        }
                    } catch (Exception e) {
                        LOGGER.error("problem loading extra info", e);
                    }
                });
    }

    private boolean personMissingIdInAssembly(final Person p) {
        return null != p && 0 == p.assemblyId && null != p.belongToAssembly && p.belongToAssembly.getId() == AssemblyEnum.WAL.getId();
    }

    private void reportProblem(final Person person, final List<WalloonRepresentativeDocument> documents) {
        LOGGER.error("error getting extra data for {}. Found {}", person, documents);
    }

    private void fillData(final Person person, final WalloonRepresentativeDocument document) {
        person.assemblyId = document.getId();
        politicianRepository.updatePolitician(person);
    }
}
