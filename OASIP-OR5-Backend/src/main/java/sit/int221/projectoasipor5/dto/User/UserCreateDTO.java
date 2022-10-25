package sit.int221.projectoasipor5.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sit.int221.projectoasipor5.entities.Role;
import sit.int221.projectoasipor5.exception.UniqueValidator.UniqueEmail;
import sit.int221.projectoasipor5.exception.UniqueValidator.UniqueName;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private Integer userId;

    @NotBlank(message = "UserName must not be blank")
    @Length(min = 1, max = 100, message="UserName must be between 1 and 100")
    @UniqueName
    private String name;

    @NotBlank(message = "UserEmail must not be blank")
    @Length(min = 1, max = 50, message="UserEmail must be between 1 and 50")
    @Email(message = "UserEmail must be a well-formed email address")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Password must not be blank")
    @NotNull(message = "Password must not be null")
    @Size(min = 8, max = 14, message = "Password must be between 8 and 14")
    private String password;

    private Role role;
}
