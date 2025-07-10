package com.eaglebank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaglebank.api.model.transaction.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findBySourceAccountIdOrderByTimestampDesc(Long accountId);

  List<Transaction> findByDestinationAccountIdOrderByTimestampDesc(Long accountId);
}