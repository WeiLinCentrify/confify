package confify.models.output;

import java.sql.Timestamp;
import java.util.List;

import confify.models.*;

/**
 * Created by Dennis on 4/26/2015.
 */
public class ConferenceOutput {
    private long id;
    private String name;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private Address venue;
    private Organization organization;
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
