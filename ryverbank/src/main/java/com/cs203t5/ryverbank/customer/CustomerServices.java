package com.cs203t5.ryverbank.customer;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;

public interface CustomerServices {
    List<Customer> listUsers();

    // Creating new User (C of C.R.U.D)
    // Roles: Manager
    /**
     * Creates a new customer.
     * 
     * @param user The customer.
     * @return The customer created.
     */
    Customer createUser(Customer user);

    /**
     * Gets the customer with the given userId.
     * 
     * @param userId                The customer's id.
     * @param authenticatedUsername The authenticated username of the given userId.
     * @param authenticatedUserRole The authenticated role of the given userId.
     * @return The customer found.
     */
    Customer getUser(Long userId, String authenticatedUsername, String authenticatedUserRole);

    // Update user's information (U of C.R.U.D)
    // Roles: Customer & Manager
    /**
     * Updates the address of the customer with the given userId. (For Managers to
     * update customer's address)
     * 
     * @param userId  The customer's id.
     * @param address The customer's new address information.
     * @return The customer with the updated address information.
     */
    Customer updateAddress(Long userId, String address);

    /**
     * Updates the address of the customer with the given userId. (For Customers
     * updating their own address)
     * 
     * @param userId                The customer's id.
     * @param address               The customer's new address information.
     * @param authenticatedUsername The authenticated customer's username.
     * @return The customer with the updated address information.
     */
    Customer updateAddress(Long userId, String address, String authenticatedUsername);

    /**
     * Updates the phone number of the customer with the given userId. (For Managers
     * to update customer's phone number)
     * 
     * @param userId The customer's id.
     * @param phone  The customer's new phone number.
     * @return The customer with the updated phone number.
     */
    Customer updatePhone(Long userId, String phone);

    /**
     * Updates the phone number of the customer with the given userId. (For
     * Customers updating their own phone number)
     * 
     * @param userId                The customer's id.
     * @param phone                 The customer's new phone number.
     * @param authenticatedUsername The authenticated customer's username.
     * @return The customer with the updated phone number.
     */
    Customer updatePhone(Long userId, String phone, String authenticatedUsername);

    // Manager update Customer password
    /**
     * Updates the password of the customer with the given userId. (For Managers to
     * update customer's password)
     * 
     * @param userId   The customer's id.
     * @param password The customer's new password.
     * @return The customer with the updated password.
     */
    Customer updatePassword(Long userId, String password);

    // Customer update own password
    /**
     * Updates the password of the customer with the given userId. (For Customers
     * updating their own password)
     * 
     * @param userId                The customer's id.
     * @param password              The customer's new password.
     * @param authenticatedUsername
     * @return The customer with the updated password.
     */
    Customer updatePassword(Long userId, String password, String authenticatedUsername);

    // Manager update Customer active field
    /**
     * Updates the account status of the customer with the given userId. (Only for
     * Managers to update customer's account status)
     * 
     * @param userId       The customer's id.
     * @param activeStatus The customer's new account status.
     * @return The customer with the updated account status.
     */
    Customer updateActiveStatus(Long userId, Boolean activeStatus);

}
