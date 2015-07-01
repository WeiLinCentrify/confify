package confify.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis on 4/19/2015.
 */
@Entity
@Table(name = "Admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column (name="password", length = 128, nullable = false)
    private String password;
    @Column(name = "firstname", length = 20, nullable = false)
    private String firstName;
    @Column(name = "lastname", length = 20, nullable = false)
    private String lastName;
    @Column (name="email", length = 100, nullable = false, unique = true)
    private String email;
    @Column (name="bio", length = 1000)
    private String bio;
    @Column (name="avatar_url", length = 100)
    private String avatarUrl;
    @Column (name="qr_url", length = 100)
    private String qrUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Organization organization;
    @OneToMany (mappedBy = "admin", cascade = CascadeType.REMOVE)
    private List<Manage> manages;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Manage> getManages() {
        return manages;
    }

    public void setManages(List<Manage> manages) {
        this.manages = manages;
    }

    /*public List<Conference> getManagedConference() {
        List<Conference> confers = new ArrayList();
        for (Manage manage : manages) {
            confers.add(manage.getConference());
        }
        return confers;
    }*/
}
