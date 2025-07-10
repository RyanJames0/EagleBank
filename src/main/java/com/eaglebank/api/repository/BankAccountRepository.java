package com.eaglebank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eaglebank.api.model.account.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

  List<BankAccount> findByUserEmail(String email);
  
  boolean existsByAccountNumber(String accountNumber);
  
  @org.springframework.data.jpa.repository.Query("SELECT MAX(b.accountNumber) FROM BankAccount b")
  String findMaxAccountNumber();
}
