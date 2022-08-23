package sit.int221.projectoasipor5.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.Utils.ListMapper;
import sit.int221.projectoasipor5.Utils.Role;
import sit.int221.projectoasipor5.dto.UserAddDTO;
import sit.int221.projectoasipor5.dto.UserDTO;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.repositories.UserRepository;

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

    public User createUser(@Valid UserAddDTO user) {
        User newUser = modelMapper.map(user, User.class);
        newUser.setName(user.getName().trim());
        newUser.setEmail(user.getEmail().trim());
        repository.saveAndFlush(newUser);
        return modelMapper.map(newUser, User.class);
    }

    public void deleteById(Integer id) {
        repository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
        repository.deleteById(id);
    }

    private User mapUser(User existingUser , User updateUser){
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setRole(updateUser.getRole());
        return existingUser;
    }
}
