package sit.int221.projectoasipor5.exception;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class HandleErrorUnsucceess {
    private String timestamp;
    private Integer status;
    private String path;
    private String message;
    private String error;
    private Map<String,String> filedErrors;
}
