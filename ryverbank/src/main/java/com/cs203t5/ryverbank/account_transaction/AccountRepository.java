package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.customer.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    //find list of account based on customer
    List<Account> findByCustomer(Optional<Customer> optional);

    // //find list of account based on customerId
    // List<Account> findByCustomerCustomerId(Long id);

    //find account based on accound id
    Optional<Account> findById(Long accId);
    
    //check if account exists
    boolean existsById(Long accId);
}
