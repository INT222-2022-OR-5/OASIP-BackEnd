package sit.int221.projectoasipor5.exception;

import org.springframework.http.HttpStatus;

public class MatchUserExceptionHandler extends Exception{
    public MatchUserExceptionHandler(HttpStatus ok, String message){
        super(message);
    }
}
