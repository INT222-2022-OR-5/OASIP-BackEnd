package sit.int221.projectoasipor5.dto.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sit.int221.projectoasipor5.entities.Role;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    @NotBlank(message = "UserName must not be blank")
    @Length(min = 1, max = 100, message="UserName must be between 1 and 100")
    private String name;

    @NotNull(message = "UserEmail must not be null")
    @NotBlank(message = "UserEmail must not be blank")
    @Email(message = "UserEmail must be a well-formed email address")
    @Length(min = 1, max = 50, message="UserEmail must be between 1 and 50")
    private String email;

    private Role role;
}
