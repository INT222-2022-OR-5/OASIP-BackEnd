package sit.int221.projectoasipor5.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = -35101209347496298L;
    private String email;
    private String password;
}