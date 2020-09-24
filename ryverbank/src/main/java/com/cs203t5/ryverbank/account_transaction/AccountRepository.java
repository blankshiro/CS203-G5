package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    //find list of account based on customer id
    List<Account> findByCustomer_id(Long cusId);

    //find account based on accound id
    Optional<Account> findById(Long accId);

    //check if account exists
    boolean existsById(Long accId);

    //
    Optional<Account> findByidAndUserid(Long accId, Long userId);
}
