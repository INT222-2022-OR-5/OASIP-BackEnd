package sit.int221.projectoasipor5.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.dto.UserLoginDTO;
import sit.int221.projectoasipor5.utils.ListMapper;
import sit.int221.projectoasipor5.dto.UserAddDTO;
import sit.int221.projectoasipor5.dto.UserDTO;
import sit.int221.projectoasipor5.dto.UserUpdateDTO;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.repositories.UserRepository;
import sit.int221.projectoasipor5.utils.Role;

import javax.validation.Valid;
import java.util.List;

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

//    public User createUser(@Valid UserAddDTO user) {
//        User newUser = modelMapper.map(user, User.class);
//        newUser.setRole(user.getRole().trim());
//        newUser.setName(user.getName().trim());
//        newUser.setEmail(user.getEmail().trim());
//        repository.saveAndFlush(newUser);
//        return modelMapper.map(newUser, User.class);
//    }

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
}
