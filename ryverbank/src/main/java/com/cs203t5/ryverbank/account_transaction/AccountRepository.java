package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.customer.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    //find list of account based on customer
    List<Account> findByCustomer(Optional<Customer> optional);

    List<Account> findAllByCustomerCustomerId(Long cusId);

    // //find list of account based on customerId
    // List<Account> findByCustomerCustomerId(Long id);

    //find account based on accound id
    Optional<Account> findById(Long accId);
    
    //check if account exists
    boolean existsById(Long accId);

    //Delete every account other than MarketMaker
    @Modifying
    @Query(value = "DELETE FROM ACCOUNT WHERE CUSTOMER_ID <> 4", nativeQuery = true)
    void deleteImmediate();
}
