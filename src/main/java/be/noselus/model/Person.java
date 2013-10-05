package be.noselus.model;

public class Person {
    public String name;
    public String firstName;

    public Person(String fullName) {
    	int pos = fullName.lastIndexOf(' ');
		this.firstName = fullName.substring(0, pos);
		this.name = fullName.substring(pos+1);
	}
    
    public Person(final String name, final String firstName) {
        this.name = name;
        this.firstName = firstName;
    }
    
    @Override
    public String toString() {
    	return firstName + " " + name;
    }
    
}
