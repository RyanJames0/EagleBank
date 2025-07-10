package com.eaglebank.api.service.transaction;

import com.eaglebank.api.dto.transaction.TransactionRequest;
import com.eaglebank.api.dto.transaction.TransactionResponse;

public interface TransactionService {

  TransactionResponse createTransaction(Long accountId, TransactionRequest request, String userEmail);

  java.util.List<TransactionResponse> getTransactionsForAccount(Long accountId, String userEmail);

  TransactionResponse getTransactionById(Long accountId, Long transactionId, String userEmail);
}