package com.cs203t5.ryverbank.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    // define a derived query to find user by username
    Optional<User> findByUsername(String username);

    // define a derived query to find user by email
    Optional<User> findByEmail(String email);

    // define a derived query to find user by username or email
    Optional<User> findByUsernameOrEmail(String username, String email);

    // define a derviced query to check if username exists
    Boolean existsByUsername(String username);

    // define a derviced query to check if email exists
    Boolean existsByEmail(String email);

    
}
