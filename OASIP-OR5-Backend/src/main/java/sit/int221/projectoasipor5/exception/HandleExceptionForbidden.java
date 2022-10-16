package sit.int221.projectoasipor5.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class HandleExceptionForbidden extends Exception{
    public HandleExceptionForbidden(String message){
        super(message);
    }
}