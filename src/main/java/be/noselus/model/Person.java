package be.noselus.model;

import java.util.List;

public class Person {

    public Integer id;
    public String full_name;
    public String party;
    public String address;
    public String postal_code;
    public String town;
    public String phone;
    public String fax;
    public String email;
    public String site;
    public PersonFunction function;
	public int assembly_id;
	public double latitude;
	public double longitude;
    public List<Integer> asked_questions;

    public Person(Integer id, String full_name, String party, String address, String postal_code, String town, String phone,
                  String fax, String email, String site, PersonFunction function, int assembly_id, double latitude, double longitude, 
                  List<Integer> askedQuestionIds) {
        this.id = id;
        this.full_name = full_name;
        this.party = party;
        this.address = address;
        this.postal_code = postal_code;
        this.town = town;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.site = site;
        this.function = function;
        this.assembly_id = assembly_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.asked_questions = askedQuestionIds;
    }
    
    @Override
    public String toString() {
    	return full_name;
    }
    
}
