package sit.int221.projectoasipor5.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.dto.User.UserCreateDTO;
import sit.int221.projectoasipor5.dto.User.UserDTO;
import sit.int221.projectoasipor5.dto.User.UserUpdateDTO;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.exception.CheckUniqueUserExceptionHandler;
import sit.int221.projectoasipor5.repositories.UserRepository;
import sit.int221.projectoasipor5.services.UserService;

import javax.validation.Valid;
import java.util.List;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository repository;

    //Get all user
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return this.userService.getAllUser();
    }

    //Get user with id
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id, WebRequest request){
        return userService.getUserById(id);
    }

    //Create user
    @PostMapping({"/signup"})
    @ResponseStatus(HttpStatus.CREATED)
    public User create( @Validated @RequestBody UserCreateDTO newUser) throws CheckUniqueUserExceptionHandler {
        return userService.createUser(newUser);
    }

    //Delete user with id
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    //Update user with id
    @PutMapping({"/{id}"})
    public ResponseEntity update(@Valid @RequestBody UserUpdateDTO updateUser, @PathVariable Integer id) throws CheckUniqueUserExceptionHandler {
        List<UserDTO> userList = getAllUsers();

        updateUser.setName(updateUser.getName().trim());
        updateUser.setEmail(updateUser.getEmail().trim());

        for(int i = 0; i < userList.size(); i++) {
            if(updateUser.getName().trim().equals(userList.get(i).getName()) && updateUser.getEmail().trim().equals(userList.get(i).getEmail())
                    && userList.get(i).getUserId() != id && userList.get(i).getUserId() != id) {
                throw new CheckUniqueUserExceptionHandler("User and Email already exist");
            }else if(updateUser.getName().trim().equals(userList.get(i).getName()) && userList.get(i).getUserId() != id){
                throw new CheckUniqueUserExceptionHandler("User name must be unique.");
            } else if(updateUser.getEmail().trim().equals(userList.get(i).getEmail()) && userList.get(i).getUserId() != id){
                throw new CheckUniqueUserExceptionHandler("User email must be unique.");
            }
        }
        User user = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));

        modelMapper.map(updateUser, user);
        repository.saveAndFlush(user);
        return ResponseEntity.status(200).body(user);
    }


}
