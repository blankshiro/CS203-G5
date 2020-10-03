package com.cs203t5.ryverbank.customer;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;

public interface CustomerService {
    List<Customer> listUsers();

    Customer getUser(Long userId, String authenticatedUsername, String authenticatedUserRole);

    Customer addUser(Customer user);

    //Update user's information

    // Customer updateUser(Long userId, Customer user, String authenticatedUsername, String authenticatedUserRole);
    Customer updateAddress(Long userId, String address);
    Customer updateAddress(Long userId, String address, String authenticatedUsername);

    Customer updatePhone(Long userId, String phone);
    Customer updatePhone(Long userId, String phone, String authenticatedUsername);

    Customer updatePassword(Long userId, String password);
    // Customer updatePassword(Long userId, String password, String authenticatedUsername);

    // Customer deactiveUser(Long userId);

    Customer createCustomer(Customer user);

    // void sendEmail(String toUser, String token);
}
