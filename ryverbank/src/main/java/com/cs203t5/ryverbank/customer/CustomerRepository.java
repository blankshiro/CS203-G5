package com.cs203t5.ryverbank.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository <Customer, Long> {
    // define a derived query to find user by username
    Optional<Customer> findByUsername(String username);
    // define a derviced query to check if username exists
    Boolean existsByUsername(String username);

    //Reset function: Delete everyone except marketMaker
    @Modifying
    @Query(value = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID <> 4", nativeQuery = true)
    void deleteAllButOne();
}
    