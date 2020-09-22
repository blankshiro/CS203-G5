package com.cs203t5.ryverbank.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, String> {
    // define a derived query to find user by username
    Optional<User> findByUsername(String username);

    // define a derived query to find user by email
    // Optional<User> findByEmail(String email);
}
