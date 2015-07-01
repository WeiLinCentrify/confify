package confify.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis on 4/19/2015.
 */
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column (name="password", length = 128, nullable = false)
    private String password;
    @Column(name = "firstname", length = 20)
    private String firstName;
    @Column(name = "lastname", length = 20)
    private String lastName;
    @Column (name="email", length = 100, nullable = false, unique = true)
    private String email;
    @Column (name="profession", length = 100)
    private String profession;
    @Column (name="organization", length = 1000)
    private String organization;
    @Column (name="bio", length = 1000)
    private String bio;
    @Column (name="avatar_url", length = 100)
    private String avatarUrl;
    @Column (name="qr_url", length = 100)
    private String qrUrl;
    @OneToMany (mappedBy = "attendee", cascade = CascadeType.REMOVE)
    private List<Attend> attends;
    @OneToMany (mappedBy = "speaker", cascade = CascadeType.REMOVE)
    private List<GiveSpeech> giveSpeeches;

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

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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

    public List<Attend> getAttends() {
        return attends;
    }

    public void setAttends(List<Attend> attends) {
        this.attends = attends;
    }

    public List<GiveSpeech> getGiveSpeeches() {
        return giveSpeeches;
    }

    public void setGiveSpeeches(List<GiveSpeech> giveSpeeches) {
        this.giveSpeeches = giveSpeeches;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    /*public List<Conference> getAttendConferences() {
        List<Conference> conferences = new ArrayList<Conference>();
        for(Attend attend : attends) {
            conferences.add(attend.getConference());
        }
        return conferences;
    }

    public List<Conference> getGiveSpeechConferences() {
        List<Conference> conferences = new ArrayList<Conference>();
        for(GiveSpeech giveSpeech : giveSpeeches) {
            conferences.add(giveSpeech.getConference());
        }
        return conferences;
    }*/
}
