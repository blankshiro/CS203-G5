package com.cs203t5.ryverbank.entity.User;

import java.util.List;

import javax.validation.Valid;
import com.cs203t5.ryverbank.entity.Security.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
public class UserController {
    private UserService userService;

    // @Autowired
    private BCryptPasswordEncoder encoder;

    
    public UserController(UserService us, BCryptPasswordEncoder encoder){
        this.userService = us;
        this.encoder = encoder;
    }
    
    //list all the user in the system
    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.listUsers();
    }

    //search for user with given id
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id){
        User user = userService.getUser(id);

        if(user == null) throw new UserNotFoundException(id);
        return userService.getUser(id);
    }

    //add a new user 
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userService.addUser(user);
    }

    //update the user details
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User newUserInfo){
        User user = userService.updateUser(id, newUserInfo);
        if(user == null) throw new UserNotFoundException(id);

        return user;
    }

    //delete an existing user
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable String id){
        try{
            userService.deleteUser(id);
        }catch(EmptyResultDataAccessException e){
            throw new UserNotFoundException(id);
        }
    }
}
