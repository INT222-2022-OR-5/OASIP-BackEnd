package sit.int221.projectoasipor5.exception;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HandleOverlappedError {
        private Date timestamp = new Date();
        private Integer status;
        private String message;
        private String path;
        private String error;
}

