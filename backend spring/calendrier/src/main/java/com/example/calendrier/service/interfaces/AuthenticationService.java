package com.example.calendrier.service.interfaces;

import com.example.calendrier.payload.request.SignUpRequest;
import com.example.calendrier.payload.request.SigninRequest;
import com.example.calendrier.payload.response.JwtAuthenticationResponse;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthenticationService {
    void register(SignUpRequest request)throws Exception;
    JwtAuthenticationResponse login(SigninRequest request) throws Exception;
    void verify(String verificationCode);
    void forgotPassword( String email) throws MessagingException, UnsupportedEncodingException;
    void changePassword(String verificationCode, String newPassword);
}
