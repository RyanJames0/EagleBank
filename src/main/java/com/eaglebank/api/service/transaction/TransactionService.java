package com.eaglebank.api.service.transaction;

import com.eaglebank.api.dto.transaction.CreateTransactionRequest;
import com.eaglebank.api.dto.transaction.ListTransactionsResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;

public interface TransactionService {

  TransactionResponse createTransaction(String accountNumber, CreateTransactionRequest request, String userEmail);
  
  ListTransactionsResponse getTransactionsForAccount(String accountNumber, String userEmail);
  
  TransactionResponse getTransactionById(String accountNumber, String transactionId, String userEmail);
}