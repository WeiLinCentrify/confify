package confify.controllers;

import confify.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Dennis on 4/20/2015.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /*@ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAppException(Exception e) {
        return new ResponseEntity("Server encounter an exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }*/

}
