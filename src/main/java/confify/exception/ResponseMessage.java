package confify.exception;

/**
 * Created by Dennis on 4/25/2015.
 */
public class ResponseMessage {
    private String message;

    public ResponseMessage(String message){
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
