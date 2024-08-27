package com.example.calendrier.config;

import com.example.calendrier.entity.User;
import com.example.calendrier.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, UserService userService, JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/user/updateUser").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/user/getuser/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/user/all").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/gettaskbyid/**").hasAnyAuthority(User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/gettaskbyemployee/**").hasAnyAuthority(User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/gettaskbycreated/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/updatetask").hasAnyAuthority(User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/user/changePassword").hasAnyAuthority(User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/getAlltasks").hasAnyAuthority(User.ERole.ROLE_EMPLOYEE.name(), User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/createtask/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/deletetask/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/task/assign/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/user/getbyrole/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/user/getbydepartement/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/user/getbyusername/**").hasAnyAuthority(User.ERole.ROLE_MANAGER.name(), User.ERole.ROLE_ADMIN.name())
                        .requestMatchers("/**").hasAuthority(User.ERole.ROLE_ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(config -> config.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
