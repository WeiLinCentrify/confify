package confify.models.output;

import confify.models.Address;
import confify.models.Organization;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Dennis on 4/29/2015.
 */
public class ConferenceWithSpeakerAndAttendee {
    private long id;
    private String name;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private Address venue;
    private Organization organization;
    private List<UserInfo> attendees;
    private List<UserInfo> speakers;

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

    public List<UserInfo> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<UserInfo> attendees) {
        this.attendees = attendees;
    }

    public List<UserInfo> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<UserInfo> speakers) {
        this.speakers = speakers;
    }
}
