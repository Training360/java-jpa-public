package jpa;

import javax.persistence.Embeddable;

@Embeddable
public class Address {

    private String zip;

    private String city;

    private String line1;

    public Address() {
    }

    public Address(String zip, String city, String line1) {
        this.zip = zip;
        this.city = city;
        this.line1 = line1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
