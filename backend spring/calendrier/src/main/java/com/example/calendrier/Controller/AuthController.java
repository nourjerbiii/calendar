package com.example.calendrier.Controller;


import com.example.calendrier.payload.request.NewPasswordRequest;
import com.example.calendrier.payload.request.SignUpRequest;
import com.example.calendrier.payload.request.SigninRequest;
import com.example.calendrier.payload.response.JwtAuthenticationResponse;
import com.example.calendrier.payload.response.MessageResponse;
import com.example.calendrier.repository.UserRepository;
import com.example.calendrier.service.EmailHelperImpl;
import com.example.calendrier.service.UserServiceImpl;
import com.example.calendrier.service.interfaces.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final UserServiceImpl userService;
    final PasswordEncoder encoder;
    final EmailHelperImpl emailHelper;
    final AuthenticationService authenticationService;
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, UserServiceImpl userService, PasswordEncoder encoder, EmailHelperImpl emailHelper, AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.encoder = encoder;
        this.emailHelper = emailHelper;
        this.authenticationService = authenticationService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SigninRequest request) {
        try {
            JwtAuthenticationResponse response = authenticationService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        try {
            authenticationService.register(request);
            return ResponseEntity.created(new URI("/register")).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @GetMapping("/verify/{code}")
    public ResponseEntity<?> verifyUser(@PathVariable String code) {
        try {
            authenticationService.verify(code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @PostMapping("/forgotpassword/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email) {
        try {
            authenticationService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @PostMapping("/resetPassword/{code}")
    public ResponseEntity<?> changePassword(@PathVariable String code, @RequestBody NewPasswordRequest newPasswordRequest) {
        try {
            authenticationService.changePassword(code, newPasswordRequest.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
