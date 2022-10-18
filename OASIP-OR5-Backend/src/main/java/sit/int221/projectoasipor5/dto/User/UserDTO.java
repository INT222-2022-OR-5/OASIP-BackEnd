package sit.int221.projectoasipor5.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sit.int221.projectoasipor5.entities.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;

    @NotBlank(message = "UserName must not be blank")
    @Length(min = 1, max = 100, message="UserName must be between 1 and 100")
    private String name;

    @NotBlank(message = "UserEmail must not be blank")
    @Length(min = 1, max = 50, message="UserEmail must be between 1 and 50")
    @Email(message = "UserEmail must be a well-formed email address")
    private String email;

    private Role role;

    private Instant createdOn;

    private Instant updatedOn;
}
