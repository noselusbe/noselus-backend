package be.noselus.fix;

import be.noselus.AbstractDbDependantTest;
import be.noselus.model.AssemblyEnum;
import be.noselus.model.PersonFunction;
import be.noselus.pictures.PictureManager;
import be.noselus.repository.AssemblyRepositoryInDatabase;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.PoliticianRepositoryInDatabase;
import be.noselus.scraping.WalloonRepresentativesFetcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("db cleaning issue")
public class PersonWithMissingAssemblyRefFixTest extends AbstractDbDependantTest {

    PoliticianRepository repo;

    @Before
    public void init() {
        repo = new PoliticianRepositoryInDatabase(AbstractDbDependantTest.dataSource,
                new AssemblyRepositoryInDatabase(AbstractDbDependantTest.dataSource),
                new PictureManager(AbstractDbDependantTest.dataSource));
    }

    @Test
    public void fixRuns(){
        repo.upsertPolitician("Will Smith", "Rock", "Fresh", "90210","Belair","001",null,null,null, PersonFunction.DEPUTY, AssemblyEnum.WAL);
        repo.upsertPolitician("John Doe", "Rock", "Fresh", "90210","Belair","001",null,null,null, PersonFunction.DEPUTY, AssemblyEnum.WAL);
        final PersonWithMissingAssemblyRefFix fix = new PersonWithMissingAssemblyRefFix(repo, new WalloonRepresentativesFetcher());
        fix.runFix();
    }

}