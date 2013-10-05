package be.noselus.model;

public class Person {
    public String full_name;
    public String party;
    public String address;
    public String postal_code;
    public String town;
    public String phone;
    public String fax;
    public String email;
    public String site;

    public Person(final String full_name) {
        this.full_name = full_name;
    }

    public Person(String full_name, String party, String address, String postal_code, String town, String phone, String fax, String email, String site) {
        this.full_name = full_name;
        this.party = party;
        this.address = address;
        this.postal_code = postal_code;
        this.town = town;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.site = site;
    }
}
