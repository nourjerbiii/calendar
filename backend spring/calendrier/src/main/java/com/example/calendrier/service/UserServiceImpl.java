package com.example.calendrier.service;

import com.example.calendrier.entity.User;
import com.example.calendrier.repository.UserRepository;
import com.example.calendrier.service.interfaces.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.javamail.JavaMailSender;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;

    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder encoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mailSender = mailSender;
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll() ;
    }

    @Override
    public List<User> findUserByRole(User.ERole role) {
        return userRepository.findUserByRole(role);
    }

    @Override
    public User blockUser(Long idUser) {
        User user =userRepository.findById(idUser).orElse(null);
        user.setBlocked(true);
        return userRepository.save(user);
    }

    @Override
    public User unBlockUser(Long idUser) {
        User user =userRepository.findById(idUser).orElse(null);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserbyId(Long idUser) {
        return userRepository.findById(idUser).orElse(null);
    }

    @Override
    public void removeUser(Long idUser) {
        userRepository.deleteById(idUser);

    }

    @Override
    public boolean changePassword(String verificationCode, String newPassword) {
        User user = userRepository.findByResetPasswordToken(verificationCode);

        if (user == null) {
            return false;
        } else {
            user.setResetPasswordToken(null);
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public boolean changePasswordByUser(Long id, String password, String newPassword) {
        User user = userRepository.findById(id).get();

        if (!encoder.matches(password, user.getPassword())) {
            return false;
        }

        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return true;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> findUsersByDepartement(User.Departement departement) {
        return userRepository.findUsersByDepartement(departement);
    }

    @Override
    public User updateEmployee(User user) {
        return userRepository.save(user);
    }
    @Override
    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "/auth");
    }



    @Override
    public void sendVerificationEmail(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        String toAddress = user.getEmail();
        String fromAddress = email;
        String senderName = "Calnedrier";
        String subject = "Veiller verifier votre compte ";
        String content = "cher [[name]],<br>"
                + "Veuillez cliquer sur le lien ci-dessous pour vérifier votre inscription :<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFIER</a></h3>"
                + "Merci,<br>"
                + "Calendrier";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = "http://localhost:4200/auth/verif/" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }
    @Override
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null) {
            return false;
        } else {
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public void sendForgotPassword(User user) throws MessagingException, UnsupportedEncodingException, MessagingException {
        String toAddress = user.getEmail();
        String fromAddress = email;
        String senderName = "Calendrier";
        String subject = "Changement de mot de passe";
        String content = "Cher [[name]],<br>"
                + "Veuillez cliquer sur le lien ci-dessous pour votre mot de passe:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">CHANGER</a></h3>"
                + "Merci,<br>"
                + "Calendrier";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getUsername());
        String changePasswordURL = "http://localhost:4200/auth/newpassword/" + user.getResetPasswordToken();
        content = content.replace("[[URL]]", changePasswordURL);
        helper.setText(content, true);
        mailSender.send(message);
    }
    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Error 322: User not found"));
    }
}
