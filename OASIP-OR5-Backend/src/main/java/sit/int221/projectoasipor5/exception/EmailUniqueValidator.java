package sit.int221.projectoasipor5.exception;

import org.springframework.beans.factory.annotation.Autowired;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.repositories.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {
    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(EmailUnique constraintAnnotation) {}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        try {
            List<User> userConstraint = repository.findEmailUnique(email);
            if (userConstraint.size() >= 1) {return false;}
            else{return true;}
        }catch(Exception ex) {
            return false;
        }
    }
}

