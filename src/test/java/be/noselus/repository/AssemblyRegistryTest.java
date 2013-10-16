package be.noselus.repository;

import be.noselus.model.Assembly;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssemblyRegistryTest {

    private AssemblyRegistry repo = new AssemblyRegistryMyBatis();

    @Test
    public void getData(){
        final Assembly id = repo.findId(1);
        assertEquals("Parlement Wallon",id.getLabel());
    }
}
