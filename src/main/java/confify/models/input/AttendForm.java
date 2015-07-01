package confify.models.input;

/**
 * Created by Dennis on 4/26/2015.
 */
public class AttendForm {
    private String email;
    private long userId;
    private long conferId;
    private int status;

    public long getConferId() {
        return conferId;
    }

    public void setConferId(long conferId) {
        this.conferId = conferId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
