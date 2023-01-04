package sit.int221.projectoasipor5.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import sit.int221.projectoasipor5.exception.CustomAccessDeniedHandler;
import sit.int221.projectoasipor5.filters.CustomAuthenticationFilter;
import sit.int221.projectoasipor5.filters.CustomAuthorizationFilter;
import sit.int221.projectoasipor5.models.JwtAuthenticationEntryPoint;
import sit.int221.projectoasipor5.services.UserService;


import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/users/**"));

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        return new Argon2PasswordEncoder(16, 29, 2, 16, 2);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(argon2PasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable().cors().and()
                .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint()).and()
                .authorizeRequests()
                .antMatchers(GET, "/api/users/**").hasAuthority("admin")
                .antMatchers(DELETE, "/api/users/**").hasAuthority("admin")
                .antMatchers(PUT, "/api/users/**").hasAuthority("admin")
                .antMatchers(PUT,"/api/eventCategory/").hasAnyAuthority("admin", "lecturer")
                .antMatchers(GET, "/api/events/").hasAnyAuthority("student", "admin", "lecturer")
                .antMatchers(POST, "/api/events/").hasAnyAuthority("student", "admin")
                .antMatchers(PUT, "/api/events/").hasAnyAuthority("student", "admin")
                .antMatchers(DELETE, "/api/events/").hasAnyAuthority("student", "admin")
                .antMatchers(POST, "/api/users/signup/**", "/api/login/**", "/api/events/guest").permitAll()
                .antMatchers(GET,"/api/refresh-token/**","/api/eventCategory/**").permitAll()
                .anyRequest().authenticated().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .addFilter(customAuthenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}