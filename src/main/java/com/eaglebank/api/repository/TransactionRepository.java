package com.eaglebank.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eaglebank.api.model.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

  List<Transaction> findByAccountNumberOrderByCreatedTimestampDesc(String accountNumber);
  
  Optional<Transaction> findByIdAndAccountNumber(String id, String accountNumber);
  
  boolean existsByAccountNumber(String accountNumber);
}