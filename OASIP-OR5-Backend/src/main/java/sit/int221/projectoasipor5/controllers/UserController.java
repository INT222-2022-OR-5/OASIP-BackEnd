package sit.int221.projectoasipor5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sit.int221.projectoasipor5.dto.UserAddDTO;
import sit.int221.projectoasipor5.dto.UserDTO;
import sit.int221.projectoasipor5.dto.UserLoginDTO;
import sit.int221.projectoasipor5.dto.UserUpdateDTO;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.services.UserService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    //Get all user
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUser();
    }

    //Get user with id
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id){
        return userService.getUserById(id);
    }

    //Create user with id
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Validated @RequestBody UserAddDTO newUser) {
        return userService.createUser(newUser);
    }

    //Delete user with id
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    //Update user with id
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id,@RequestBody UserUpdateDTO updateUser){
        return userService.updateUser(updateUser, id);
    }

    //Login
    @PostMapping("/match")
    public User match(@Valid @RequestBody UserLoginDTO user){
        return userService.match(user);
    }

}
