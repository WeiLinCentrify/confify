package confify.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Dennis on 4/19/2015.
 */
@Entity
@Table(name = "Organization")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column (name="name", length = 20, nullable = false)
    private String name;
    @Column (name="address", length = 50, nullable = false)
    private String address;
    @Column (name="introduction", length = 3000, nullable = false)
    private String introduction;
    @Column (name="img_url", length = 100)
    private String imgUrl;
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<Admin> admins;
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<Conference> conferences;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Admin> admins) {
        this.admins = admins;
    }

    public List<Conference> getConferences() {
        return conferences;
    }

    public void setConferences(List<Conference> conferences) {
        this.conferences = conferences;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
