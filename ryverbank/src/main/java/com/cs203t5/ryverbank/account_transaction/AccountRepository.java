package com.cs203t5.ryverbank.account_transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findByid(Long accId);
    Optional<Account> findByidAndUserid(Long accId, Long userId);
}
