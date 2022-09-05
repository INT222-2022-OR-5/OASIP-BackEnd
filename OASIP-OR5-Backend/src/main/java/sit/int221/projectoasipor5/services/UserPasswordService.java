package sit.int221.projectoasipor5.services;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordService implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
        String secuePass = argon2PasswordEncoder.encode(rawPassword);
        return secuePass;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
        Boolean matches = argon2PasswordEncoder.matches(rawPassword, encodedPassword);
        return matches;
    }
}
