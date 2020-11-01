package com.cs203t5.ryverbank.customer;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService class is used to retrieve user related data.
 * 
 * @see UserDetailsService
 */
@Service
public class CustomUserDetailsService implements UserDetailsService{
    /** The user repository. */
    private CustomerRepository users;
    
    /**
     * Sole constructor for CustomDetailsService.
     * 
     * @param users The user repository.
     */
    public CustomUserDetailsService(CustomerRepository users) {
        this.users = users;
    }

    /**
     * To return a UserDetails for Spring Security Note that the method takes only a
     * username. The UserDetails interface has methods to get the password.
     */
    @Override
    public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException {
        return users.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
