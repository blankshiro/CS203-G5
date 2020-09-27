package com.cs203t5.ryverbank.customer;

import java.util.List;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {
    private CustomerRepository users;
    private CustomerService userService;
    private BCryptPasswordEncoder encoder;

    public CustomerController(CustomerRepository users, CustomerService userSvc, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.userService = userSvc;
        this.encoder = encoder;
    }

    /**
     * Registers a new user and uses BCrypt encoder to encrypt the password for
     * storage
     * 
     * @param user
     * @return the user
     */
    @PostMapping("/customers")
    public Customer createCustomer(@Valid @RequestBody Customer user) {
        System.out.println("TEST1: " + user.validateNric(user.getNric()));
        System.out.println("TEST2: " + user.validatePhone(user.getPhone()));
        if (!user.validateNric(user.getNric()) || !user.validatePhone(user.getPhone())) {
            throw new InvalidEntryException("Invalid NRIC/Phone number");

        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userService.createCustomer(user);

    }

    /**
     * List all users in the system
     * 
     * @return list of all users
     */
    @GetMapping("/customers")
    public List<Customer> getUsers() {
        return users.findAll();
    }

    /**
     * Search for user with the given id If there is not user with the given "id",
     * throw a UserNotFoundException
     * 
     * @param id
     * @return user with the given id
     */
    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        Customer user = userService.getUser(id);

        if (user == null)
            throw new CustomerNotFoundException(id);
        return userService.getUser(id);
    }

    /**
     * If there is no user with the given id, throw a UserNotFoundException
     * 
     * @param idssss
     * @param newUserInfo
     * @return the updated, or newly added book
     */
    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer newUserInfo) {
        Customer user = userService.updateUser(id, newUserInfo);
        if (user == null)
            throw new CustomerNotFoundException(id);

        return user;
    }

    /**
     * Removes a user with the DELETE request to "/users/{id}" If there is no user
     * with the given id, throw a UserNotFoundException
     * 
     * @param id
     */
    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomerNotFoundException(id);
        }
    }
}
