package com.cs203t5.ryverbank.customer;

/**
 * An interface for various customer services.
 */
public interface CustomerService {
    /**
     * Creates a new user. If the username exists in the customer repository, throw
     * a CustomerExistsException. If the phone number and nric is invalid, throw a
     * InvalidEntryException.
     * 
     * @param user The user to be created.
     * @return The user created.
     */
    Customer createUser(Customer user);

    /**
     * Gets the customer with the specified user id, authenticated username and
     * authenticated user role. If the user calling this method is not a manager or
     * the authenticated user, throw CustomerUnauthorizedException.
     * 
     * @param userId                The user id.
     * @param authenticatedUsername The authenticated username.
     * @param authenticatedUserRole The authenticated user role.
     * @return The customer found.
     */
    Customer getUser(Long userId, String authenticatedUsername, String authenticatedUserRole);

    /**
     * Updates the customer's address with the specified user id and new address
     * information. If no user is found, return null.
     * 
     * @param userId     The user id.
     * @param address The user's new address information.
     * @return The customer with the updated information.
     */
    Customer updateAddress(Long userId, String address);

    /**
     * Updates the customer's phone number with the specified user id and new phone
     * number. If no user is found, return null.
     * 
     * @param userId   The user id.
     * @param phone The user's new phone information.
     * @return The customer with the updated information.
     */
    Customer updatePhone(Long userId, String phone);

    /**
     * Updates the customer's password with the specified user id and new password .
     * If no user is found, return null.
     * 
     * @param userId      The user id.
     * @param password The user's new password.
     * @return The customer with the updated information.
     */
    Customer updatePassword(Long userId, String password);

    /**
     * Updates the user's active status with the specified user id and new active
     * status. If no user is found, return null.
     * 
     * @param userId       The user id.
     * @param activeStatus The user's new active status.
     * @return The customer with the updated active status.
     */
    Customer updateActiveStatus(Long userId, Boolean activeStatus);

}
