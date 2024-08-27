package com.example.calendrier.service;

import com.example.calendrier.entity.User;
import com.example.calendrier.service.interfaces.EmailHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailHelperImpl implements EmailHelper {
    @Value("${spring.mail.username}")
    private String email;
    private final JavaMailSender mailSender;
    @Override
    public void sendVerificationEmail(User user) {
        try {
            String toAddress = user.getEmail();
            String fromAddress = email;
            String senderName = "Calendar";
            String subject = "Please verify your account";
            String url="http://localhost:4200/auth/verify/"+user.getVerificationCode();
            String content = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f0f8ff; color: #333; }"
                    + "h3 { color: #007bff; }"
                    + ".container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #fff; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<p>Dear [[name]],</p>"
                    + "<p>Please click on the link below to verify your registration:</p>"
                    + "<h3><a href=\"[[URL]]\" style=\"text-decoration: none; color: #007bff;\" target=\"_self\">VERIFY</a></h3>"
                    + "<p>Thank you,</p>"
                    + "<p>Calendar.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            CreateAndSendMessage(user, toAddress, fromAddress, senderName, subject, content, mailSender, url);
        } catch (MailSendException e) {
            // Handle MailSendException separately with a descriptive message
            throw new MailSendException("Error 201: sending verification email. Mail server issue or recipient address may be invalid."+ e.getMessage());
        } catch (UnsupportedEncodingException e) {
            // Handle UnsupportedEncodingException with a descriptive message
            throw new RuntimeException("Error 202: sending verification email. Unsupported encoding."+ e.getMessage());
        } catch (MessagingException e) {
            // Handle MessagingException with a descriptive message
            throw new RuntimeException("Error 203: sending verification email. Messaging exception occurred."+ e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions and rethrow with a generic message
            throw new RuntimeException("Error 204: sending verification email. An unexpected exception occurred."+ e.getMessage());
        }
    }
    @Override
    public void sendForgotPassword(User user) {
        try {
            String toAddress = user.getEmail();
            String fromAddress = email;
            String senderName = "Calendar";
            String subject = "Password Reset";
            String url="http://localhost:4200/auth/newpassword/"+user.getResetPasswordToken();
            String content = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f0f8ff; color: #333; }"
                    + "h3 { color: #007bff; }"
                    + ".container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #fff; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<p>Dear [[name]],</p>"
                    + "<p>Please click on the link below to reset your password:</p>"
                    + "<h3><a href=\"[[URL]]\" style=\"text-decoration: none; color: #007bff;\" target=\"_self\">RESET</a></h3>"
                    + "<p>Thank you,</p>"
                    + "<p>Calendar.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            CreateAndSendMessage(user, toAddress, fromAddress, senderName, subject, content, mailSender, url);
        } catch (MailSendException e) {
            throw new MailSendException("Error 205: sending password reset email. Mail server issue or recipient address may be invalid."+ e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error 206: sending password reset email. Unsupported encoding."+ e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException("Error 207: sending password reset email. Messaging exception occurred."+ e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error 208: sending password reset email. An unexpected exception occurred."+ e.getMessage());
        }
    }
    static void CreateAndSendMessage(User user, String toAddress, String fromAddress, String senderName, String subject, String content, JavaMailSender mailSender, String url) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getUsername());
        content = content.replace("[[URL]]", url);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
