package com.cs203t5.ryverbank.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository <Customer, Long> {
    // define a derived query to find user by username
    Optional<Customer> findByUsername(String username);

    // define a derviced query to check if username exists
    Boolean existsByUsername(String username);
}
