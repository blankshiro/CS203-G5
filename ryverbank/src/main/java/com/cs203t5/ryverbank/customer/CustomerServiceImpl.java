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
    public Customer getUser(Long userId,String authenticatedUsername, String authenticatedUserRole) {
      
        return users.findById(userId).map(user -> {
            if(authenticatedUserRole.equals("ROLE_USER") && user.getUsername().equals(authenticatedUsername) || authenticatedUserRole.equals("ROLE_MANAGER")){
                return users.save(user);
            }
            else{
                throw new CustomerUnauthorizedException(userId);
            }
        }).orElse(null);
    }

    @Override
    public Customer addUser(Customer user) {
        return users.save(user);
    }

     //Updates the address of a particular user
    //This method will be used exclusively by Managers
    @Override
    public Customer updateAddress(Long userId, String newAddress){
        if(newAddress != null && !newAddress.isEmpty()){
            return users.findById(userId).map(user -> {
                user.setAddress(newAddress);
                return users.save(user);
            }).orElse(null);
        }

        return null;

    }

    //Updates the address of a particular user
    //This method will be used exclusively by Customer
    @Override
    public Customer updateAddress(Long userId, String newAddress, String authenticatedUsername){
        if(newAddress != null && !newAddress.isEmpty()){
            return users.findById(userId).map(user -> {
                if(user.getUsername().equals(authenticatedUsername)){
                    user.setAddress(newAddress);
                    return users.save(user);
                }else{
                    throw new CustomerUnauthorizedException("You do not have permission to access this information");
                } 
            }).orElse(null);
        }

        return null;
    }

     //Updates the phone of a particular user
    //This method will be used exclusively by Managers
    @Override
    public Customer updatePhone(Long userId, String newPhone){
        if(newPhone != null && !newPhone.isEmpty()){
            return users.findById(userId).map(user -> {
                user.setPhone(newPhone);
                return users.save(user);
            }).orElse(null);
        }

        return null;
    }

     //Updates the phone of a particular user
    //This method will be used exclusively by Customer
    @Override
    public Customer updatePhone(Long userId, String newPhone, String authenticatedUsername){
        if(newPhone != null && !newPhone.isEmpty()){
            return users.findById(userId).map(user -> {
                if(user.getUsername().equals(authenticatedUsername)){
                    user.setPhone(newPhone);
                    return users.save(user);
                }else{
                    throw new CustomerUnauthorizedException("You do not have permission to access this information");
                } 
            }).orElse(null);
        }

        return null;
    }

    //Updates the password of a particular user
    //This method will be used exclusively by Managers
    @Override
    public Customer updatePassword(Long userId, String newPassword){
        if(newPassword != null && !newPassword.isEmpty()){
            return users.findById(userId).map(user -> {
                // System.out.println(newPassword + " hi");
                // String encodedPassword = encoder.encode(newPassword);
                // System.out.println(encodedPassword);
                user.setPassword(newPassword);
                return users.save(user);
            }).orElse(null);
        }

        return null;
    }


    // @Override
    // public Customer updateUser(Long userId, Customer newUserInfo, String authenticatedUsername, String authenticatedUserRole) {
    //     return users.findById(userId).map(user -> {
    //         //If phone it not being updated, it will remains the old phone number


    //         if((authenticatedUserRole.equals("ROLE_USER") && !(user.getUsername().equals(authenticatedUsername)))){
    //             throw new CustomerUnauthorizedException(userId);
    //         }
    //         if(authenticatedUserRole.equals("ROLE_MANAGER")){
    //             try{
    //                 newUserInfo.getPhone().equals(null);
    //                 user.setPhone(newUserInfo.getPhone());
    //             }catch(NullPointerException e){
    //                 user.setPhone(user.getPhone());
    //             }
    
    //              //If address it not being updated, it will remains the old address
                    
    //             try{
    //                 newUserInfo.getAddress().equals(null);
    //                 user.setAddress(newUserInfo.getAddress());
    //             }catch(NullPointerException e){
    //                 user.setAddress(user.getAddress());
    //             }
               
    //             //Manager disable accounts
    //             try{
    //                 if(newUserInfo.getActive().equals(null));
    //                 user.setActive(newUserInfo.getActive());
    //             }catch(NullPointerException e){
    //                 user.setActive(user.getActive());
    //             }
    //         }else if(authenticatedUserRole.equals("ROLE_USER") && user.getUsername().equals(authenticatedUsername)){
    //             try{
    //                 newUserInfo.getPhone().equals(null);
    //                 user.setPhone(newUserInfo.getPhone());
    //             }catch(NullPointerException e){
    //                 user.setPhone(user.getPhone());
    //             }
    //              //If address it not being updated, it will remains the old address
                    
    //             try{
    //                 newUserInfo.getAddress().equals(null);
    //                 user.setAddress(newUserInfo.getAddress());
    //             }catch(NullPointerException e){
    //                 user.setAddress(user.getAddress());
    //             }
              
    //             //Customers are not allowed to disable account
    //             try{
    //                 if(newUserInfo.getActive().equals(null));
    //                 throw new CustomerUnauthorizedException(userId);
    //             }catch(NullPointerException e){
                    
    //             }

               

    //         }
       


    //         //If password it not being updated, it will remains the old password
    //         // try{
    //         //     newUserInfo.getPassword().equals(null);
    //         //     System.out.println("hi");
    //         //     user.setPassword(newUserInfo.getPassword());
    //         // }catch(NullPointerException e){
    //         //     user.setPassword(user.getPassword());
    //         // }

         
    //         return users.save(user);
    //     }).orElse(null);
    // }



    
    @Override
    public Customer createCustomer(Customer user) {
        if (users.existsByUsername(user.getUsername())) {
            throw new CustomerExistsException("username used");
        }
        
        //user.setPassword(encoder.encode(user.getPassword()));
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
