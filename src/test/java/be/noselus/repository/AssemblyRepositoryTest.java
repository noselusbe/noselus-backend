package be.noselus.repository;

import be.noselus.AbstractDbDependantTest;
import be.noselus.model.Assembly;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class AssemblyRepositoryTest extends AbstractDbDependantTest {

    private AssemblyRepository repo;

    @Before
    public void setUp() {
        repo = new AssemblyRepositoryDbUtils(AbstractDbDependantTest.dataSource);
    }

    @Test
    public void assemblyFoundWhenPresent(){
        final Assembly assembly = repo.findId(1);
        assertThat(assembly, notNullValue());
        assertThat(assembly.getId(), is(1));
        assertThat(assembly.getLabel(), is("Parlement Wallon"));
        assertThat(assembly.getLevel(), is(Assembly.Level.REGION));
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionWhenNotFound(){
        repo.findId(42);
    }

    @Test
    public void returnAllTheAssemblies(){
        final List<Assembly> assemblies = repo.getAssemblies();
        assertThat(assemblies, hasItems(new Assembly(1,"Parlement Wallon", Assembly.Level.REGION),
                new Assembly(2, "Chambre des représentants", Assembly.Level.DEPUTY_CHAMBER),
                new Assembly(3, "Parlement Bruxellois", Assembly.Level.REGION),
                new Assembly(4, "Parlement de la Fédération Wallonie Bruxelles", Assembly.Level.COMMUNITY),
                new Assembly(5, "Sénat", Assembly.Level.SENAT),
                new Assembly(6, "Gouvernement Wallon", Assembly.Level.REGION),
                new Assembly(7, "Gouvernement Fédéral", Assembly.Level.FEDERAL)));
    }

}
