package confify.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by Dennis on 4/20/2015.
 */
public class ErrorMessage {
    private String message;

    public ErrorMessage(String message){
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
