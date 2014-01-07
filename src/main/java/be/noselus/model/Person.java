package be.noselus.model;

import java.util.List;

public class Person {

    public Integer id;
    public String fullName;
    public String party;
    public String address;
    public String postalCode;
    public String town;
    public String phone;
    public String fax;
    public String email;
    public String site;
    public PersonFunction function;
	public int assemblyId;
	public AssemblyEnum assembly;
	public double latitude;
	public double longitude;
    public List<Integer> asked_questions;
    public Assembly belongToAssembly;

    public Person(Integer id, String fullName, String party, String address, String postalCode, String town, String phone,
                  String fax, String email, String site, PersonFunction function, int assemblyId, List<Integer> askedQuestionIds,
                  Assembly belongToAssembly, double latitude, double longitude) {

        this.id = id;
        this.fullName = fullName;
        this.party = party;
        this.address = address;
        this.postalCode = postalCode;
        this.town = town;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.site = site;
        this.function = function;
        this.assemblyId = assemblyId;
        this.assembly = AssemblyEnum.getFromAssemblyId(belongToAssembly.getId());
        this.belongToAssembly = belongToAssembly;
        this.latitude = latitude;
        this.longitude = longitude;
        this.asked_questions = askedQuestionIds;
    }
    
    @Override
    public String toString() {
    	return fullName;
    }
    
}
