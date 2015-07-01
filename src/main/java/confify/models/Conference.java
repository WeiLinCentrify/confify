package confify.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Dennis on 4/19/2015.
 */
@Entity
@Table(name = "Conference")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column (name="name", length = 20, nullable = false)
    private String name;
    @Column (name="description", length = 2000)
    private String description;
    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;
    @Column(name = "end_time", nullable = false)
    private Timestamp endTime;
    @Embedded
    private Address venue;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Organization organization;
    @OneToMany (mappedBy = "conference", cascade = CascadeType.REMOVE)
    private List<Manage> manages;
    @OneToMany (mappedBy = "conference", cascade = CascadeType.REMOVE)
    private List<Attend> attends;
    @OneToMany (mappedBy = "conference", cascade = CascadeType.REMOVE)
    private List<GiveSpeech> giveSpeeches;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Address getVenue() {
        return venue;
    }

    public void setVenue(Address venue) {
        this.venue = venue;
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
}
