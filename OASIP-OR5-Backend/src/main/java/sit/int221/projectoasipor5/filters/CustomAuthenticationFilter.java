package sit.int221.projectoasipor5.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import sit.int221.projectoasipor5.exception.HandleErrorUnsucceess;
import sit.int221.projectoasipor5.models.JwtRequest;
import sit.int221.projectoasipor5.repositories.UserRepository;


import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final String secret = "secret";

    // ปรับ access token จาก 30 นาที เหลือ 1 นาทีเพื่อให้ง่ายต่อการตรวจ
    private final Integer jwtExpirationInMs = 1 * 60 * 1000;

    private final Integer refreshExpirationDateInMs = 2 * 60 * 1000;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        JwtRequest login;
        try {
            login = new Gson().fromJson(request.getReader(), JwtRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert webApplicationContext != null;
        UserRepository userRepository = webApplicationContext.getBean(UserRepository.class);

        if (!userRepository.existsByEmail(login.getEmail())) {
            request.setAttribute("error", "email not found");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));

        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpirationDateInMs))
                .withIssuer(request.getRequestURI().toString())
                .sign(algorithm);

        String role = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).toString();
        role = role.substring(1, role.length() - 1);
        System.out.println(role);

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert webApplicationContext != null;
        UserRepository userRepository = webApplicationContext.getBean(UserRepository.class);

        sit.int221.projectoasipor5.entities.User username = userRepository.findByEmail(user.getUsername());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("message", "Login Successfully");
        tokens.put("email", user.getUsername());
        tokens.put("name", username.getName());
        tokens.put("role", role);
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {

        String error = (String) request.getAttribute("error");

        HandleErrorUnsucceess errors;
        Map<String, String> errorMap = new HashMap<>();
        Date timestamp = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (error != null) {
            if (error.equals("email not found")) {
                errorMap.put("Authentication", "Email not found");
            }
        } else {
            errorMap.put("Authentication", "Password is incorrect");
        }

        errors = new HandleErrorUnsucceess(sdf3.format(timestamp), error != null ? HttpStatus.NOT_FOUND.value() : HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI(), "Validation", error != null ? "Not Found" : "Unauthorized" , errorMap);
        response.setStatus(error != null ? HttpStatus.NOT_FOUND.value() : HttpStatus.UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), errors);
    }
}
