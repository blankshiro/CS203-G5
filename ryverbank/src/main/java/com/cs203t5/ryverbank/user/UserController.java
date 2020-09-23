package com.cs203t5.ryverbank.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private UserRepository users;
    private UserService userService;
    private BCryptPasswordEncoder encoder;

    public UserController(UserRepository users, UserService userSvc, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.userService = userSvc;
        this.encoder = encoder;
    }


    /**
     * Registers a new user and uses BCrypt encoder to encrypt the password for storage
     * 
     * @param user
     * @return the user
     */
    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    /**
     * List all users in the system
     * 
     * @return list of all users
     */
    @GetMapping("/customers")
    public List<User> getUsers() {
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
    public User getUser(@PathVariable Long id) {
        User user = userService.getUser(id);

        if (user == null)
            throw new UserNotFoundException(id);
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
    public User updateUser(@PathVariable Long id, @RequestBody User newUserInfo) {
        User user = userService.updateUser(id, newUserInfo);
        if (user == null)
            throw new UserNotFoundException(id);

        return user;
    }

    /**
     * Removes a user with the DELETE request to "/users/{id}" If there is no user
     * with the given id, throw a UserNotFoundException
     * 
     * @param id
     */
    @DeleteMapping("/customers/{id}")
    public void deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(id);
        }
    }
}
