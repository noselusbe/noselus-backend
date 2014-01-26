package be.noselus.model;

public class PersonSmall {

    public String fullName;
    public Integer id;

    public PersonSmall(final String fullName) {
        this.fullName = fullName;
        this.id = 0;
    }

    public PersonSmall(final String fullName, final Integer id) {
        this.fullName = fullName;
        this.id = id;
    }

    @Override
    public String toString() {
        return fullName;
    }

    public static PersonSmall fromPerson(final Person person) {
        return new PersonSmall(person.fullName, person.id);
    }
}
