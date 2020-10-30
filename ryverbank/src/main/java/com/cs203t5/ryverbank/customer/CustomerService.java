package com.cs203t5.ryverbank.customer;

/**
 * An interface for various customer services.
 */
public interface CustomerService {
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

    // Manager update Customer phone
    Customer updatePhone(Long userId, String phone);

    // Manager update Customer password
    Customer updatePassword(Long userId, String password);

    // Manager update Customer active field
    Customer updateActiveStatus(Long userId, Boolean activeStatus);


}
