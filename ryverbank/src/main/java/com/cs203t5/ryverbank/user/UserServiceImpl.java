package com.cs203t5.ryverbank.user;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;
// import com.cs203t5.ryverbank.email.*;

// import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository users;
    // private ConfirmationTokenService confirmationTokenService;
    private BCryptPasswordEncoder encoder;
    // private EmailService javaMailSender;

    public UserServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public List<User> listUsers() {
        return users.findAll();
    }
    
    @Override
    public User getUser(String id) {
        return users.findById(id).orElse(null);
    }

    @Override
    public User addUser(User user) {
        return users.save(user);
    }

    @Override
    public User updateUser(String id, User newUserInfo) {
        return users.findById(id).map(user -> {
            user.setPassword(newUserInfo.getPassword());
            return users.save(user);
        }).orElse(null);
    }

    @Override
    public void deleteUser(String id) {
        users.deleteById(id);
    }

    /*
    public void register(User user) {

        String encryptedPassword = encoder.encode(user.getPassword());

        user.setPassword(encryptedPassword);

        users.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenService.saveToken(confirmationToken);

        sendEmail(user.getEmail(), confirmationToken.getTokenid());

    }
    */

    /*
    public void confirm(ConfirmationToken confirmationToken) {
        // Gets the user from the token
        User user = confirmationToken.getUser();

        // enables user
        user.setEnabled(true);

        // save user to repo
        users.save(user);

        // deletes the confirmation token from repo
        confirmationTokenService.deleteToken(confirmationToken.getTokenid());
    }
    */

    /**
     * Sends confirmation email to the user who registered for an account
     */
    /*
    public void sendEmail(String toUser, String token) {
        System.out.println("sending email...");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("planfor.ryberbank.gmail.com");
        simpleMailMessage.setTo(toUser);
        simpleMailMessage.setSubject("Confirmation Link for RyverBank!");
        simpleMailMessage
                .setText("Thank you for registering with RyverBank! Please click on the link to activate your account"
                        + "http://localhost:8080/signup/confirm" + token);

        javaMailSender.sendEmail(simpleMailMessage);

        System.out.println("confirmation email sent!");
    }
    */

}
