package com.cs203t5.ryverbank.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A CustomerRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on customer objects
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Optional query to find user by username
     * 
     * @param username The username of the customer.
     * @return The customer found.
     */
    Optional<Customer> findByUsername(String username);

    /**
     * Derviced query to check if username exists
     * 
     * @param username The username of the customer.
     * @return True if user is found, otherwise return False.
     */
    Boolean existsByUsername(String username);
}
