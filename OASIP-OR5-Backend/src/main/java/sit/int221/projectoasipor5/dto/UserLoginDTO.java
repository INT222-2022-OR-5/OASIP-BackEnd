package sit.int221.projectoasipor5.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    private Integer id;

    @Email(message="Email must be a well-formed email address")
    @NotBlank(message = "Email must not be blank")
    @Length(min = 1, max = 50, message="Email must be between 1 and 50")
    private String email;

    @NotNull(message = "Password must not be null")
    @Length(min = 8, max = 14, message="Password must be between 8 and 14")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }
}
