package sit.int221.projectoasipor5.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sit.int221.projectoasipor5.entities.Role;
import sit.int221.projectoasipor5.exception.EmailUnique;
import sit.int221.projectoasipor5.exception.EnumValidator;
import sit.int221.projectoasipor5.exception.NameUnique;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddDTO {
    @NameUnique
    @NotBlank(message = "UserName must not be blank")
    @Length(min = 1, max = 100, message="UserName must be between 1 and 100")
    private String name;

    @EmailUnique
    @Email(message = "UserEmail must be a well-formed email address")
    @NotBlank(message = "UserEmail must not be blank")
    @Length(min = 1, max = 50, message="UserEmail must be between 1 and 50")
    private String email;

    @NotNull(message = "Password must not be null")
    @Length(min = 8, max = 14, message="Password must be between 8 and 14")
    private String password;

    @NotNull(message = "Role must not be null")
    @EnumValidator(enumClass = Role.class)
    private String role = "student";

    public void setName(String name){
        this.name = name.trim();
    }
    public String getName(){
        return this.name;
    }

    public void setEmail(String email){
        this.email = email.trim();
    }
    public String getEmail(){
        return this.email;
    }

    public void setRole(String role){
        this.role = role;
        if(role.trim() == ""){
            this.role = "student";
        }
    }
}
