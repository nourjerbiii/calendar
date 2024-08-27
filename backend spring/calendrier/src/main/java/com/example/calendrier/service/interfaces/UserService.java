package com.example.calendrier.service.interfaces;

import com.example.calendrier.entity.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface UserService {
    List<User> findAllUser();
    List<User> findUserByRole(User.ERole role);
    User blockUser(Long idUser);
    User unBlockUser(Long idUser);
    User updateUser (User user);
    User findUserbyId(Long idUser);
    void removeUser(Long idUser);
    boolean changePassword(String verificationCode, String newPassword);
    boolean changePasswordByUser(Long id , String password , String newPassword);
    User findByUsername(String username);
    List<User> findUsersByDepartement(User.Departement departement);
    User updateEmployee(User user);

    String getSiteURL(HttpServletRequest request);

    void sendVerificationEmail(User user, String siteURL) throws UnsupportedEncodingException, MessagingException;

    boolean verify(String verificationCode);

    void sendForgotPassword(User user) throws MessagingException, UnsupportedEncodingException, MessagingException;

    UserDetailsService userDetailsService();
}
