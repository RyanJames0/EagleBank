package com.eaglebank.api.service.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.api.dto.transaction.CreateTransactionRequest;
import com.eaglebank.api.dto.transaction.ListTransactionsResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.model.transaction.Transaction;
import com.eaglebank.api.model.transaction.TransactionType;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;
import com.eaglebank.api.repository.UserRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired private TransactionRepository transactionRepository;
  @Autowired private BankAccountRepository bankAccountRepository;
  @Autowired private UserRepository userRepository;

  @Override
  @Transactional
  public TransactionResponse createTransaction(String accountNumber, CreateTransactionRequest request, String userEmail) {
    // Verify account exists and belongs to user
    Optional<BankAccount> accountOpt = bankAccountRepository.findByAccountNumberAndUserEmail(accountNumber, userEmail);
    
    if (accountOpt.isEmpty()) {
      if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
        throw new SecurityException("Account does not belong to authenticated user");
      } else {
        throw new IllegalArgumentException("Account not found");
      }
    }
    
    BankAccount account = accountOpt.get();
    
    // Get user ID for transaction
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    // Validate transaction
    if (request.getType() == TransactionType.withdrawal) {
      if (account.getBalance().compareTo(request.getAmount()) < 0) {
        throw new IllegalArgumentException("Insufficient funds");
      }
    }
    
    // Generate transaction ID in format tan-[A-Za-z0-9]+
    String transactionId = "tan-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    
    // Create transaction
    Transaction transaction = new Transaction();
    transaction.setId(transactionId);
    transaction.setAmount(request.getAmount());
    transaction.setCurrency(request.getCurrency());
    transaction.setType(request.getType());
    transaction.setReference(request.getReference());
    transaction.setUserId(user.getId());
    transaction.setAccountNumber(accountNumber);
    
    // Update account balance
    BigDecimal newBalance;
    if (request.getType() == TransactionType.deposit) {
      newBalance = account.getBalance().add(request.getAmount());
    } else {
      newBalance = account.getBalance().subtract(request.getAmount());
    }
    account.setBalance(newBalance);
    
    // Save both transaction and updated account
    Transaction savedTransaction = transactionRepository.save(transaction);
    bankAccountRepository.save(account);
    
    return new TransactionResponse(savedTransaction);
  }

  @Override
  public ListTransactionsResponse getTransactionsForAccount(String accountNumber, String userEmail) {
    // Verify account exists and belongs to user
    Optional<BankAccount> accountOpt = bankAccountRepository.findByAccountNumberAndUserEmail(accountNumber, userEmail);
    
    if (accountOpt.isEmpty()) {
      if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
        throw new SecurityException("Account does not belong to authenticated user");
      } else {
        throw new IllegalArgumentException("Account not found");
      }
    }
    
    List<Transaction> transactions = transactionRepository.findByAccountNumberOrderByCreatedTimestampDesc(accountNumber);
    List<TransactionResponse> transactionResponses = transactions.stream()
        .map(TransactionResponse::new)
        .collect(Collectors.toList());
    
    return new ListTransactionsResponse(transactionResponses);
  }

  @Override
  public TransactionResponse getTransactionById(String accountNumber, String transactionId, String userEmail) {
    // Verify account exists and belongs to user
    Optional<BankAccount> accountOpt = bankAccountRepository.findByAccountNumberAndUserEmail(accountNumber, userEmail);
    
    if (accountOpt.isEmpty()) {
      if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
        throw new SecurityException("Account does not belong to authenticated user");
      } else {
        throw new IllegalArgumentException("Account not found");
      }
    }
    
    // Find transaction
    Optional<Transaction> transactionOpt = transactionRepository.findByIdAndAccountNumber(transactionId, accountNumber);
    
    if (transactionOpt.isEmpty()) {
      throw new IllegalArgumentException("Transaction not found");
    }
    
    return new TransactionResponse(transactionOpt.get());
  }
}