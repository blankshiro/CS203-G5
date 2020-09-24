package com.cs203t5.ryverbank.customer;

import java.util.List;
import java.util.regex.Pattern;

// import com.cs203t5.ryverbank.token.*;
// import com.cs203t5.ryverbank.email.*;

// import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository users;
    // private ConfirmationTokenService confirmationTokenService;
    private BCryptPasswordEncoder encoder;
    // private EmailService javaMailSender;

    public CustomerServiceImpl(CustomerRepository users) {
        this.users = users;
    }

    @Override
    public List<Customer> listUsers() {
        return users.findAll();
    }
    
    @Override
    public Customer getUser(Long userId) {
        return users.findById(userId).orElse(null);
    }

    @Override
    public Customer addUser(Customer user) {
        return users.save(user);
    }

    @Override
    public Customer updateUser(Long userId, Customer newUserInfo) {
        return users.findById(userId).map(user -> {
            user.setPassword(newUserInfo.getPassword());
            return users.save(user);
        }).orElse(null);
    }

    @Override
    public void deleteUser(Long userId) {
        users.deleteById(userId);
    }

    
    @Override
    public Customer createCustomer(Customer user) {
        if (users.existsByUsername(user.getUsername())) {
            throw new CustomerExistsException("username used");
        }
        
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

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
