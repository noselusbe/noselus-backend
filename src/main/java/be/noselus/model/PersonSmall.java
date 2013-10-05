package be.noselus.model;

public class PersonSmall {

    public String full_name;
    public Integer id;

    public PersonSmall(final String full_name) {
    	this.full_name = full_name;
    	this.id = 0;
	}
    
    public PersonSmall(final String full_name, final Integer id) {
        this.full_name = full_name;
        this.id = id;
    }
    
    @Override
    public String toString() {
    	return full_name;
    }
}
