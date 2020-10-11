package com.cs203t5.ryverbank.customer;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;

public interface CustomerServices {
    List<Customer> listUsers();

    // Creating new User (C of C.R.U.D)
    // Roles: Manager
    Customer createUser(Customer user);

    // Get User (R of C.R.U.D)
    // Roles: Customer & Manager
    Customer getUser(Long userId, String authenticatedUsername, String authenticatedUserRole);

    // Update user's information (U of C.R.U.D)
    // Roles: Customer & Manager

    // Manager update Customer Address
    Customer updateAddress(Long userId, String address);

    // Customer update own Address
    Customer updateAddress(Long userId, String address, String authenticatedUsername);

    // Manager update Customer phone
    Customer updatePhone(Long userId, String phone);

    // Customer update own phone
    Customer updatePhone(Long userId, String phone, String authenticatedUsername);

    // Manager update Customer password
    Customer updatePassword(Long userId, String password);

    // Customer update own password
    Customer updatePassword(Long userId, String password, String authenticatedUsername);

    // Manager update Customer active field
    Customer updateActiveStatus(Long userId, Boolean activeStatus);

}
