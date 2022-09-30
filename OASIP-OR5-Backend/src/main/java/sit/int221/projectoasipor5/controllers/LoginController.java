package sit.int221.projectoasipor5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import sit.int221.projectoasipor5.dto.UserLoginDTO;
import sit.int221.projectoasipor5.services.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping({"/api/login"})
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity Login(@Valid @RequestBody UserLoginDTO user, HttpServletResponse HttpServletResponse, ServletWebRequest request) throws Exception {
        return userService.Login(user , HttpServletResponse , request);
    }
}
