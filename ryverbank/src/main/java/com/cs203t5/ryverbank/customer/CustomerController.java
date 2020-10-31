package com.cs203t5.ryverbank.customer;

import java.util.*;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * A CustomerController that accepts and returns customer JSON data.
 */
@RestController
public class CustomerController {
    /** The customer repository */
    private CustomerRepository users;

    /** The customer services. */
    private CustomerService userService;

    /** A BCrypt password encoder. */
    private BCryptPasswordEncoder encoder;

    /**
     * Constructs a CustomerController with the following parameters.
     * 
     * @param users   The customer repository.
     * @param userSvc The customer services.
     * @param encoder The encoder to encode passwords.
     */
    public CustomerController(CustomerRepository users, CustomerService userSvc, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.userService = userSvc;
        this.encoder = encoder;
    }

    /**
     * Registers a new user and uses BCrypt encoder to encrypt the password for
     * storage. This method should only be accessible by the manager. If the user is
     * unauthorized, the method will throw a CustomerUnauthorizedException.
     * 
     * @param user           The user to create.
     * @param authentication Checks for user authenticated role.
     * @return The created user.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/customers")
    public Customer createCustomer(@Valid @RequestBody Customer user, Authentication authentication) {
        String authenticatedUserRole = authentication.getAuthorities().stream().findAny().get().getAuthority();

        if (authenticatedUserRole.equals("ROLE_MANAGER")) {
            user.setPassword(encoder.encode(user.getPassword()));
            return userService.createUser(user);
        } else {
            throw new CustomerUnauthorizedException("You do not have the permission to create a Customer!");
        }

    }

    /**
     * List all users in the system.
     * 
     * @return The list of all users
     */
    @GetMapping("/customers")

    public List<Customer> getUsers() {
        return users.findAll();
    }

    /**
     * Search for the user with the given id. This method should only be accessible
     * by the manager or the authenticated user. If there is no user with the given
     * "id", throw a CustomerNotFoundException.
     * 
     * @param id             The id of the user to get.
     * @param authentication Checks for the user's authenticated username and user
     *                       role.
     * @return The user found.
     */
    @GetMapping("/customers/{id}")
    public Customer getUser(@PathVariable Long id, Authentication authentication) {
        String authenticatedUserRole = authentication.getAuthorities().stream().findAny().get().getAuthority();
        String authenticatedUsername = authentication.getName();

        // Gets the user based on the authenticated username + role.
        Customer user = userService.getUser(id, authenticatedUsername, authenticatedUserRole);

        // If user is null, throw CustomerNotFoundException.
        if (user == null)
            throw new CustomerNotFoundException(id);
        return userService.getUser(id, authenticatedUsername, authenticatedUserRole);
    }

    /**
     * Updates customer's information based on the new information given. This
     * method should only be accessible to the manager or the authenticated user,
     * with the exception of deactivate the customer's account.
     * 
     * @param id             The id of the customer.
     * @param newUserInfo    The customer's new information.
     * @param authentication Checks for the user's authenticated username and user
     *                       role.
     * @return The updated customer's information.
     */

    @PutMapping("/customers/{id}")
    public Optional<Customer> updateUser(@PathVariable Long id, @RequestBody Customer newUserInfo,
            Authentication authentication) {
        String authenticatedUserRole = authentication.getAuthorities().stream().findAny().get().getAuthority();
        String authenticatedUsername = authentication.getName();

        Optional<Customer> optionalCustomer = users.findByUsername(authenticatedUsername);

        Customer user = optionalCustomer.get();

        // If the user does not exist, return error 404 handled by
        // CustomerNotFoundException
        if (!users.existsById(id))
            throw new CustomerNotFoundException(id);

        /*
         * If the Json input passed in is not null for the fields, it means that someone
         * wishes to edit the fields This same process is repeated for every field that
         * is available for updates.
         */
        // If the input passed into the Json is not null for the "address" field, it
        // means that someone wishes to update the address

        if (newUserInfo.getAddress() != null || newUserInfo.getPhone() != null || newUserInfo.getPassword() != null) {
            if (authenticatedUserRole.equals("ROLE_USER") && user.getUsername().equals(authenticatedUsername)) {
                if (user.getCustomerId() != id) {
                    throw new CustomerUnauthorizedException("Unauthorized.");
                }
                userService.updateAddress(id, newUserInfo.getAddress());
                userService.updatePhone(id, newUserInfo.getPhone());
                userService.updatePassword(id, newUserInfo.getPassword());
            }

            if (authenticatedUserRole.equals("ROLE_MANAGER")) {
                userService.updateAddress(id, newUserInfo.getAddress());
                userService.updatePhone(id, newUserInfo.getPhone());
                userService.updatePassword(id, newUserInfo.getPassword());
                userService.updateActiveStatus(id, newUserInfo.getActive());
            }
        }
        return users.findById(id);
    }

}
