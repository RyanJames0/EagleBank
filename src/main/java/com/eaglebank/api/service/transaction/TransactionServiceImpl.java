package com.eaglebank.api.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.api.dto.transaction.TransactionRequest;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.model.transaction.Transaction;
import com.eaglebank.api.model.transaction.TransactionType;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private BankAccountRepository bankAccountRepository;

  @Override
  @Transactional
  public TransactionResponse createTransaction(final Long accountId, final TransactionRequest request, final String userEmail) {
    // Find the account
    BankAccount account = bankAccountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    // Verify account ownership
    if (!account.getUserId().getEmail().equals(userEmail)) {
      throw new IllegalArgumentException("Account does not belong to authenticated user");
    }

    // Validate transaction type
    if (request.getType() != TransactionType.DEPOSIT && request.getType() != TransactionType.WITHDRAWAL) {
      throw new IllegalArgumentException("Only DEPOSIT and WITHDRAWAL transactions are supported");
    }

    // Create transaction
    Transaction transaction = new Transaction();
    transaction.setAmount(request.getAmount());
    transaction.setType(request.getType());
    transaction.setTimestamp(LocalDateTime.now());

    BigDecimal newBalance;

    if (request.getType() == TransactionType.DEPOSIT) {
      // For deposits: sourceAccount = null, destinationAccount = target account
      transaction.setSourceAccount(null);
      transaction.setDestinationAccount(account);
      
      // Update balance
      newBalance = account.getBalance().add(request.getAmount());
      account.setBalance(newBalance);
      
    } else { // WITHDRAWAL
      // Check sufficient funds
      if (account.getBalance().compareTo(request.getAmount()) < 0) {
        throw new IllegalArgumentException("Insufficient funds for withdrawal");
      }
      
      // For withdrawals: sourceAccount = target account, destinationAccount = null
      transaction.setSourceAccount(account);
      transaction.setDestinationAccount(null);
      
      // Update balance
      newBalance = account.getBalance().subtract(request.getAmount());
      account.setBalance(newBalance);
    }

    // Save transaction and updated account
    transaction = transactionRepository.save(transaction);
    bankAccountRepository.save(account);

    // Create response
    TransactionResponse response = new TransactionResponse();
    response.setId(transaction.getId());
    response.setType(transaction.getType());
    response.setAmount(transaction.getAmount());
    response.setTimestamp(transaction.getTimestamp());
    response.setAccountId(accountId);
    response.setNewBalance(newBalance);

    return response;
  }

  @Override
  public List<TransactionResponse> getTransactionsForAccount(final Long accountId, final String userEmail) {
    // Find the account and verify ownership
    BankAccount account = bankAccountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    // Verify account belongs to the user
    if (!account.getUserId().getEmail().equals(userEmail)) {
      throw new IllegalArgumentException("Account does not belong to authenticated user");
    }

    // Get all transactions for this account (both as source and destination)
    List<Transaction> sourceTransactions = transactionRepository.findBySourceAccountIdOrderByTimestampDesc(accountId);
    List<Transaction> destinationTransactions = transactionRepository.findByDestinationAccountIdOrderByTimestampDesc(accountId);

    // Combine and convert to response DTOs
    List<TransactionResponse> allTransactions = sourceTransactions.stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());

    allTransactions.addAll(destinationTransactions.stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList()));

    // Sort by timestamp descending (most recent first)
    allTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

    return allTransactions;
  }

  private TransactionResponse convertToResponse(final Transaction transaction) {
    TransactionResponse response = new TransactionResponse();
    response.setId(transaction.getId());
    response.setType(transaction.getType());
    response.setAmount(transaction.getAmount());
    response.setTimestamp(transaction.getTimestamp());
    
    // Set account ID based on transaction type
    if (transaction.getSourceAccount() != null) {
      response.setAccountId(transaction.getSourceAccount().getId());
    } else if (transaction.getDestinationAccount() != null) {
      response.setAccountId(transaction.getDestinationAccount().getId());
    }
    
    return response;
  }

  @Override
  public TransactionResponse getTransactionById(final Long accountId, final Long transactionId, final String userEmail) {
    // First, verify the account exists and belongs to the user
    BankAccount account = bankAccountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    // Verify account belongs to the user
    if (!account.getUserId().getEmail().equals(userEmail)) {
      throw new IllegalArgumentException("Account does not belong to authenticated user");
    }

    // Find the transaction
    Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

    // Verify the transaction is associated with the specified account
    boolean isAssociatedWithAccount = false;
    
    if (transaction.getSourceAccount() != null && transaction.getSourceAccount().getId() == accountId) {
      isAssociatedWithAccount = true;
    } else if (transaction.getDestinationAccount() != null && transaction.getDestinationAccount().getId() == accountId) {
      isAssociatedWithAccount = true;
    }

    if (!isAssociatedWithAccount) {
      throw new IllegalArgumentException("Transaction not found");
    }

    // Convert to response DTO
    TransactionResponse response = convertToResponse(transaction);
    response.setAccountId(accountId);
    
    return response;
  }
}