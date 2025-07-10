package com.eaglebank.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eaglebank.api.model.account.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

  List<BankAccount> findByUserEmail(String email);
  
  boolean existsByUserEmail(String email);
  
  boolean existsByAccountNumber(String accountNumber);
  
  Optional<BankAccount> findByAccountNumberAndUserEmail(String accountNumber, String userEmail);
  
  @Query("SELECT MAX(b.accountNumber) FROM BankAccount b")
  String findMaxAccountNumber();
}
