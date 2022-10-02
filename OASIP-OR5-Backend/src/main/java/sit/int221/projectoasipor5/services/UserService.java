package sit.int221.projectoasipor5.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.config.JwtTokenUtil;
import sit.int221.projectoasipor5.dto.UserLoginDTO;
import sit.int221.projectoasipor5.exception.HandleValidationErrors;
import sit.int221.projectoasipor5.models.JwtResponse;
import sit.int221.projectoasipor5.utils.ListMapper;
import sit.int221.projectoasipor5.dto.UserAddDTO;
import sit.int221.projectoasipor5.dto.UserDTO;
import sit.int221.projectoasipor5.dto.UserUpdateDTO;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserPasswordService passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    public List<UserDTO> getAllUser() {
        List<User> users = repository.findAll((Sort.by("name").ascending()));
        return listMapper.mapList(users, UserDTO.class, modelMapper);
    }

    public UserDTO getUserById(Integer id){
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User id " + id +
                        "Does Not Exist !!!"));
        return modelMapper.map(user, UserDTO.class);
    }

    public User createUser(@Valid UserAddDTO newUser) {
        User user = modelMapper.map(newUser, User.class);
        user.setName(newUser.getName().trim());
        user.setEmail(newUser.getEmail().trim());
        user.setRole(newUser.getRole());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));

        List<User> name = repository.findNameUnique(newUser.getName().trim().toLowerCase());
        if(name.size() != 0 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username is already in use");
        }
        List<User> email = repository.findEmailUnique(newUser.getEmail().trim().toLowerCase());
        if(email.size() != 0 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already in use");
        }

//        user.setPassword("Protected Field");
        repository.saveAndFlush(user);
        return modelMapper.map(newUser, User.class);
    }

    public void deleteById(Integer id) {
        repository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
        repository.deleteById(id);
    }

    public User updateUser(UserUpdateDTO updateUser, Integer id) {
        updateUser.setName(updateUser.getName().trim());
        updateUser.setEmail(updateUser.getEmail().trim());
        User user = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        if (checkIsNotUnique(updateUser, user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name or email must unique");
        }
        user = repository.findById(id).map(
                o -> mapUser(o, updateUser)).orElseGet(() -> {
            updateUser.setId(id);
            return modelMapper.map(updateUser, User.class);
        });
        return repository.saveAndFlush(user);
    }

    private boolean checkIsNotUnique(UserUpdateDTO updateUser, User user){
        List<User> users = repository.findAll();
        boolean[] isNotUnique = {false};
        users.forEach(
                e -> {
                    if (user.getId() != e.getId()) {
                        if (updateUser.getName().toLowerCase().equals(e.getName().toLowerCase()) ||
                                updateUser.getEmail().toLowerCase().equals(e.getEmail().toLowerCase())){
                            isNotUnique[0] = true;
                        }
                    }
                }
        );
        return isNotUnique[0];
    }

    public User match(UserLoginDTO user){
        if(matchPassword(user)){
            throw new ResponseStatusException(HttpStatus.OK, "User password match");
        }else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED , "User password not match");
    }

    public boolean matchPassword(UserLoginDTO user){
        User user1 = modelMapper.map(user , User.class);
        User match = repository.matchPass(user.getEmail());
        if(match == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND , "Email does not exist");
        }
        return passwordEncoder.matches(user1.getPassword() ,match.getPassword());
    }

    private User mapUser(User existingUser , UserUpdateDTO updateUser){
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setRole(updateUser.getRole());
        return existingUser;
    }

    public ResponseEntity Login (UserLoginDTO user, HttpServletResponse httpServletResponse, ServletWebRequest request) throws Exception {
        Map<String,String> errorMap = new HashMap<>();
        String status;

        if (repository.existsByEmail(user.getEmail())) {
            User userdb = repository.findByEmail(user.getEmail());
            if (passwordEncoder.matches(user.getPassword(), userdb.getPassword())) {
                errorMap.put("message", "Password Matched");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                status = HttpStatus.OK.toString();

                authenticate(user.getEmail() , user.getPassword());
                authenticate(user.getEmail(), user.getPassword());

                final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

                final String token = jwtTokenUtil.generateToken(userDetails);
                final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                return ResponseEntity.ok(new JwtResponse("Login Successfully", token, refreshToken));

            } else {
                errorMap.put("message", "Password NOT Matched");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                status = HttpStatus.UNAUTHORIZED.toString();
            }
        } else {
            errorMap.put("message", "A user with the specified email DOES NOT exist");
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            status = HttpStatus.NOT_FOUND.toString();
        }
        HandleValidationErrors errors = new HandleValidationErrors(
                Date.from(Instant.now()),
                httpServletResponse.getStatus(),
                request.getRequest().getRequestURI(),
                status,
                errorMap.get("message"));
        return ResponseEntity.status(httpServletResponse.getStatus()).body(errors);
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
