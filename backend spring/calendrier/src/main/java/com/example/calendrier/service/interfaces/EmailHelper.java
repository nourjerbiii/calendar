package com.example.calendrier.service.interfaces;

import com.example.calendrier.entity.User;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailHelper {
    void sendVerificationEmail(User user) throws UnsupportedEncodingException, MessagingException;
    void sendForgotPassword(User user) throws UnsupportedEncodingException, MessagingException;
}
