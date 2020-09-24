package com.cs203t5.ryverbank.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findById(Long accId);
    Optional<Account> findByIdAndUserID(Long accId, Long userId);
}
