package com.cs203t5.ryverbank.customer;

import java.util.List;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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

    // @GetMapping("/user")
    // public String loggedInUserInfo(Authentication authentication){
    //     String userName = authentication.getName();
    //     String role = authentication.getAuthorities().stream().findAny().get().getAuthority();
    //     return role;
    // }


    /**
     * Registers a new user and uses BCrypt encoder to encrypt the password for
     * storage
     * 
     * @param user
     * @return the user
     */
    @PostMapping("/customers")
    public Customer createCustomer(@Valid @RequestBody Customer user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.validateNric(user.getNric());
        user.validatePhone(user.getPhone());
        return users.save(user);
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
    public Customer getUser(@PathVariable Long id, Authentication authentication) {
        String authenticatedUserRole = authentication.getAuthorities().stream().findAny().get().getAuthority();
        String authenticatedUsername = authentication.getName();
        Customer user = userService.getUser(id, authenticatedUsername, authenticatedUserRole);

        if (user == null)
            throw new CustomerNotFoundException(id);
        return userService.getUser(id,  authenticatedUsername, authenticatedUserRole);
    }

    /**
     * If there is no user with the given id, throw a UserNotFoundException
     * 
     * @param idssss
     * @param newUserInfo
     * @return the updated, or newly added book
     */

    @PutMapping("/customers/{id}")
    public Customer updateUser(@PathVariable Long id, @RequestBody Customer newUserInfo, Authentication authentication) {
        String authenticatedUserRole = authentication.getAuthorities().stream().findAny().get().getAuthority();
        String authenticatedUsername = authentication.getName();
        Customer user = userService.updateUser(id, newUserInfo, authenticatedUsername, authenticatedUserRole);
        if (user == null)
            throw new CustomerNotFoundException(id);

        return user;
    }

 

    // @PutMapping(path = "/customers/{id}")
   
    // public Customer deactiveUser(@PathVariable Long id) {
            
    //         Customer user = userService.deactiveUser(id);
    //         if(user == null)
    //             throw new CustomerNotFoundException(id);
            
    //         return user;     
        
    // }


    /**
     * Removes a user with the DELETE request to "/users/{id}" If there is no user
     * with the given id, throw a UserNotFoundException
     * 
     * @param id
     */
    // @PutMapping("/customers/{id}")
    // public void deactiveUser(@PathVariable Long id) {
    //     try {
    //         userService.deactiveUser(id);
    //     } catch (EmptyResultDataAccessException e) {
    //         throw new CustomerNotFoundException(id);
    //     }
    // }
}
