package com.example.calendrier.service;

import com.example.calendrier.entity.MembreEntreprise;
import com.example.calendrier.entity.User;
import com.example.calendrier.payload.request.SignUpRequest;
import com.example.calendrier.payload.request.SigninRequest;
import com.example.calendrier.payload.response.JwtAuthenticationResponse;
import com.example.calendrier.repository.MembreEntrepriseRepository;
import com.example.calendrier.repository.UserRepository;
import com.example.calendrier.service.interfaces.AuthenticationService;
import com.example.calendrier.service.interfaces.EmailHelper;
import com.example.calendrier.service.interfaces.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailHelper emailHelper;
    private final MembreEntrepriseRepository meR;

    @Override
    public void register(SignUpRequest request) throws Exception {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Error 101: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Error 102: Email already exists!");
        }
        if (userRepository.existsByMatricule(request.getMatricule())) {
            throw new Exception("Error: National ID is already taken!");
        }
        MembreEntreprise me = meR.findByMatricule(request.getMatricule());
        if (me == null) {
            throw new Exception("Error: Wrong matricule or you are not a member of the company");
        }
        try{
            var user = User.builder()
                    .nom(me.getLastName())
                    .prenom(me.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(User.ERole.valueOf(me.getRole()))
                    .username(request.getUsername())
                    .verificationCode(RandomString.make(64))
                    .isBlocked(false)
                    .matricule(request.getMatricule())
                    .build();
            emailHelper.sendVerificationEmail(user);
            userRepository.save(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error 103: During registration. Database access exception."+ e.getMessage());
        }
    }
    @Override
    public JwtAuthenticationResponse login(SigninRequest request) throws Exception {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                User userCheck = userRepository.findByUsername(request.getUsername()).orElse(null);
                if (userCheck != null && userCheck.getVerificationCode() != null) {
                    throw new Exception("Error 104: Account not yet verified!");
                }
                if (userCheck.isBlocked()) {
                    throw new Exception("Erreur: Ce compte est bloqué!\n veuillez contacter le responsable RH, directement ou a traver notre email: Go4Dev@outlook.com.");
                }
                if (!userCheck.isApproved()) {
                    throw new Exception("Erreur: Ce compte n'est pas encore approuvé!\n veuillez contacter le responsable RH, directement ou a traver notre email: Go4Dev@outlook.com.");
                }
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                var user = userRepository.findByUsername(request.getUsername()).orElse(null);
                var jwt = jwtService.generateToken(user);
                return JwtAuthenticationResponse.builder()
                        .token(jwt)
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .username(user.getUsername())
                        .build();
            }else {
                throw new Exception("Error 120: Invalid username!");
            }
        } catch (DisabledException e) {
            throw new RuntimeException("Error 105: Your account has been disabled. Please contact support."+ e.getMessage());
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Error 106: Invalid password."+ e.getMessage());
        } catch (LockedException e) {
            throw new RuntimeException("Error 107: Your account is locked. Please contact support."+ e.getMessage());
        } catch (AccountExpiredException e) {
            throw new RuntimeException("Error 108: Your account has expired."+ e.getMessage());
        } catch (CredentialsExpiredException e) {
            throw new RuntimeException("Error 109: Your credentials have expired. Please update your password."+ e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error 110: During login. Database access exception."+ e.getMessage());
        }
    }
    @Override
    public void verify(String verificationCode) {
        try {
            User user = userRepository.findByVerificationCode(verificationCode);

            if (user == null) {
                throw new Exception("Error 112: Verification code not found.");
            } else {
                user.setVerificationCode(null);
                userRepository.save(user);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Error 113: During verification. Database access exception."+ e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error 114: During verification. An unexpected exception occurred."+ e.getMessage());
        }
    }
    @Override
    public void forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        try{
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw new RuntimeException("Error 115: User not found for the provided email address");
            }
            String randomCode = RandomString.make(64);
            user.setResetPasswordToken(randomCode);
            userRepository.save(user);
            emailHelper.sendForgotPassword(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error 116: During forgotPassword. Database access exception."+ e.getMessage());
        }
    }
    @Override
    public void changePassword(String verificationCode, String newPassword) {
        try {
            User user = userRepository.findByResetPasswordToken(verificationCode);

            if (user == null) {
                throw new Exception("Error 117: Reset password code not found.");
            } else {
                user.setResetPasswordToken(null);
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Error 118: During password change. Database access exception."+ e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error 119: During password change. An unexpected exception occurred."+ e.getMessage());
        }
    }
}
