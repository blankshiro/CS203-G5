package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.customer.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A AccountRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on account objects
 */
@Transactional
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * Derived query to find all the accounts based on customer.
     * 
     * @param optional The customer.
     * @return The list of accounts that belong to the customer.
     */
    List<Account> findByCustomer(Optional<Customer> optional);

    /**
     * Derived query to find all the accounts based on customer id.
     * 
     * @param cusId The customer id.
     * @return The list of accounts that belong to the customer id.
     */
    List<Account> findAllByCustomerCustomerId(Long cusId);

    /**
     * Optional query to find the account based on the account id.
     * 
     * @param accId The account id.
     * @return The account found.
     */
    Optional<Account> findById(Long accId);
    
    /**
     * Query to check if the account exists based on the account id.
     * 
     * @param accId The account id.
     * @return True if the account exists.
     */
    boolean existsById(Long accId);

    // Delete every account other than MarketMaker
    @Modifying
    @Query(value = "DELETE FROM ACCOUNT WHERE CUSTOMER_ID <> 4", nativeQuery = true)
    void deleteImmediate();
}
