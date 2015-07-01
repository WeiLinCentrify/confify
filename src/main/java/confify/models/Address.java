package confify.models;

import javax.persistence.*;

/**
 * Created by Dennis on 4/19/2015.
 */
@Embeddable
public class Address {
    @Column(name = "street", length = 50)
    private String street;
    @Column(name = "city", length = 50)
    private String city;
    @Column(name = "state", length = 50)
    private String state;
    @Column(name = "zip", length = 10)
    private String zip;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
