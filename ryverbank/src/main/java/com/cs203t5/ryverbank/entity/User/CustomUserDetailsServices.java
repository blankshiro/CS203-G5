package com.cs203t5.ryverbank.entity.User;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServices implements UserDetailsService{
    private UserRepository userDatabase;


    public CustomUserDetailsServices(UserRepository userDatabase){
        this.userDatabase = userDatabase;
    }

    @Override
    public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException {
        return userDatabase.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
