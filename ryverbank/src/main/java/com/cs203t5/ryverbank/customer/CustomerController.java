package com.cs203t5.ryverbank.customer;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
public class CustomerController {
    private CustomerRepository users;
    private CustomerServices userService;
    private BCryptPasswordEncoder encoder;

    public CustomerController(CustomerRepository users, CustomerServices userSvc, BCryptPasswordEncoder encoder) {
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

      // This method should only be accessible to managers/user, with the
    // exception being approving the content
    /*
     * This method will be in charge of calling all the updating methods on User
     * 
     * Roles that can call these methods: User, Manager 
     * updateAddress()
     * updatePhone() 
     * updatePassword() 
     * 
     * This method disable customer 
     * 
     * Roles that can call these methods: Manager 
     * disableCustomer()
     */

    @PutMapping("/customers/{id}")
    public Optional<Customer>  updateUser(@PathVariable Long id, @RequestBody Customer newUserInfo, Authentication authentication) {
        String authenticatedUserRole = authentication.getAuthorities().stream().findAny().get().getAuthority();
        String authenticatedUsername = authentication.getName();
        // Customer user = userService.updateUser(id, newUserInfo, authenticatedUsername, authenticatedUserRole);

        //If the user does not exist, return error 404 handled by CustomerNotFoundException
        if (!users.existsById(id))
            throw new CustomerNotFoundException(id);
        

        /*
            If the Json input passed in is not null for the fields, it means that someone wishes to edit the fields
            This same process is repeated for every field that is available for updates.
        */
        //If the input passed into the Json is not null for the "address" field, it means that someone wishes to update the address
          
        if(newUserInfo.getAddress() != null) {

            //If the authenticated role is user, we need to pass in the username to validate that the user is updating own profile
            if(authenticatedUserRole.equals("ROLE_USER")){
                userService.updateAddress(id, newUserInfo.getAddress(), authenticatedUsername);
                
            }
            else if(authenticatedUserRole.equals("ROLE_MANAGER")){
                userService.updateAddress(id, newUserInfo.getAddress());
            }
        }

        //If the input passed into the Json is not null for the "phone" field, it means that someone wishes to update the phone
        if(newUserInfo.getPhone() != null) {
            if(authenticatedUserRole.equals("ROLE_USER")){
                userService.updatePhone(id, newUserInfo.getPhone(), authenticatedUsername);
                
            }
            else if(authenticatedUserRole.equals("ROLE_MANAGER")){
                userService.updatePhone(id, newUserInfo.getPhone());
            }
        }
        
           //If the input passed into the Json is not null for the "password" field, it means that someone wishes to update the password
           if(newUserInfo.getPassword() != null) {
               if(newUserInfo.getPassword().length() < 8){
                   throw new InvalidEntryException("Password should be at least 8 characters");
               }else{
                    if(authenticatedUserRole.equals("ROLE_USER")){
                        userService.updatePassword(id, newUserInfo.getPassword(), authenticatedUsername);
                        
                    }
                    else if(authenticatedUserRole.equals("ROLE_MANAGER")){
                        userService.updatePassword(id, newUserInfo.getPassword());
                    }   
                   
               }
            
        }

        if(newUserInfo.getActive() != null){
            if(authenticatedUserRole.equals("ROLE_MANAGER")){
                userService.updateActiveStatus(id, newUserInfo.getActive());
            }
            else{
                throw new CustomerUnauthorizedException("You do not have permission to access this information");
            }
        }
        

        return users.findById(id);
    }

 

   

 
}
