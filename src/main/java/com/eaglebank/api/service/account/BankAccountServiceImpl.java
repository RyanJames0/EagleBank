package com.eaglebank.api.service.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.api.dto.account.BankAccountRequest;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;
import com.eaglebank.api.service.user.UserService;

@Service
public class BankAccountServiceImpl implements BankAccountService {
  private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);
  
  @Autowired private BankAccountRepository bankAccountRepository;
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private UserService userService;

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public BankAccountResponse createBankAccount(BankAccountRequest accountRequest) {
    logger.info("Creating bank account for user: {}", accountRequest.getEmail());
    
    try {
      User user = userService.getUserByEmail(accountRequest.getEmail());
      if (user == null) {
        logger.error("User not found: {}", accountRequest.getEmail());
        throw new IllegalArgumentException("User not found");
      }
      
      BankAccount bankAccount = new BankAccount();
      
      // Generate sequential account number with retry mechanism
      String accountNumber = generateSequentialAccountNumberWithRetry();
      bankAccount.setAccountNumber(accountNumber);
      bankAccount.setAccountType(accountRequest.getAccountType());
      bankAccount.setBalance(BigDecimal.ZERO);
      bankAccount.setSortCode("40-47-84"); // Eagle Bank sort code
      
      // Set expiry date (5 years from now for debit cards)
      bankAccount.setExpiryDate(LocalDate.now().plusYears(5));
      bankAccount.setUser(user);
      
      BankAccount savedAccount = bankAccountRepository.save(bankAccount);
      logger.info("Successfully created bank account with number: {} for user: {}",
                  accountNumber, accountRequest.getEmail());
      
      return new BankAccountResponse(savedAccount);
      
    } catch (DataIntegrityViolationException e) {
      logger.error("Data integrity violation while creating account for user: {}",
                   accountRequest.getEmail(), e);
      throw new IllegalStateException("Failed to create account due to data conflict", e);
    } catch (Exception e) {
      logger.error("Unexpected error creating account for user: {}",
                   accountRequest.getEmail(), e);
      throw new RuntimeException("Failed to create bank account", e);
    }
  }

  private String generateSequentialAccountNumberWithRetry() {
    int maxRetries = 5;
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        String accountNumber = generateSequentialAccountNumber();
        
        // Double-check uniqueness before returning
        if (!bankAccountRepository.existsByAccountNumber(accountNumber)) {
          return accountNumber;
        }
        
        logger.warn("Generated account number {} already exists, retrying... (attempt {}/{})",
                    accountNumber, attempt, maxRetries);
        
        // Small delay to reduce contention
        Thread.sleep(10 * attempt);
        
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Account number generation interrupted", e);
      }
    }
    
    throw new IllegalStateException("Failed to generate unique account number after " + maxRetries + " attempts");
  }

  private String generateSequentialAccountNumber() {
    // Format: 20XXXXXX (starts with 20, then 6 sequential digits)
    String maxAccountNumber = bankAccountRepository.findMaxAccountNumber();
    
    if (maxAccountNumber == null) {
      // First account
      return "20000001";
    }
    
    // Extract the sequential part and increment
    String sequentialPart = maxAccountNumber.substring(2); // Remove "20" prefix
    long nextNumber = Long.parseLong(sequentialPart) + 1;
    
    // Format with leading zeros to maintain 6 digits
    return String.format("20%06d", nextNumber);
  }

  @Override
  public BankAccountResponse getBankAccountById(String accountId) {
    logger.debug("Retrieving bank account by ID: {}", accountId);
    
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
      
      if (bankAccount == null) {
        logger.warn("Bank account not found with ID: {}", accountId);
        throw new IllegalArgumentException("Bank account not found");
      }
      
      logger.debug("Successfully retrieved bank account: {}", bankAccount.getAccountNumber());
      return new BankAccountResponse(bankAccount);
      
    } catch (NumberFormatException e) {
      logger.error("Invalid account ID format: {}", accountId);
      throw new IllegalArgumentException("Invalid account ID format: " + accountId);
    }
  }

  @Override
  public BankAccountResponse getBankAccountByIdForUser(String accountId, String userEmail) {
    logger.debug("Retrieving bank account {} for user: {}", accountId, userEmail);
    
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
      
      if (bankAccount == null) {
        logger.warn("Bank account not found with ID: {} for user: {}", accountId, userEmail);
        throw new IllegalArgumentException("Bank account not found");
      }
      
      // Verify account belongs to the user
      if (!bankAccount.getUserId().getEmail().equals(userEmail)) {
        logger.warn("Unauthorized access attempt: User {} tried to access account {} owned by {}",
                    userEmail, accountId, bankAccount.getUserId().getEmail());
        throw new SecurityException("Access denied: Account does not belong to authenticated user");
      }
      
      logger.debug("Successfully retrieved bank account: {} for user: {}",
                   bankAccount.getAccountNumber(), userEmail);
      return new BankAccountResponse(bankAccount);
      
    } catch (NumberFormatException e) {
      logger.error("Invalid account ID format: {} for user: {}", accountId, userEmail);
      throw new IllegalArgumentException("Invalid account ID format: " + accountId);
    }
  }

  @Override
  public List<BankAccountResponse> getBankAccountsForUser(String userEmail) {
    logger.debug("Retrieving all bank accounts for user: {}", userEmail);
    
    List<BankAccount> bankAccounts = bankAccountRepository.findByUserEmail(userEmail);
    
    logger.debug("Found {} bank accounts for user: {}", bankAccounts.size(), userEmail);
    
    return bankAccounts.stream()
        .map(BankAccountResponse::new)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public BankAccountResponse updateBankAccountForUser(String accountId, BankAccountRequest accountRequest, String userEmail) {
    logger.info("Updating bank account {} for user: {}", accountId, userEmail);
    
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
      
      if (bankAccount == null) {
        logger.warn("Bank account not found with ID: {} for user: {}", accountId, userEmail);
        throw new IllegalArgumentException("Bank account not found");
      }
      
      // Verify account belongs to the user
      if (!bankAccount.getUserId().getEmail().equals(userEmail)) {
        logger.warn("Unauthorized update attempt: User {} tried to update account {} owned by {}",
                    userEmail, accountId, bankAccount.getUserId().getEmail());
        throw new SecurityException("Access denied: Account does not belong to authenticated user");
      }
      
      // Update account type if provided
      if (accountRequest.getAccountType() != null) {
        logger.debug("Updating account type from {} to {} for account: {}",
                     bankAccount.getAccountType(), accountRequest.getAccountType(), accountId);
        bankAccount.setAccountType(accountRequest.getAccountType());
      }
      
      // Note: We don't update email as that would change ownership
      // Balance should only be updated through transactions, not direct updates
      
      bankAccount = bankAccountRepository.save(bankAccount);
      logger.info("Successfully updated bank account: {} for user: {}",
                  bankAccount.getAccountNumber(), userEmail);
      
      return new BankAccountResponse(bankAccount);
      
    } catch (NumberFormatException e) {
      logger.error("Invalid account ID format: {} for user: {}", accountId, userEmail);
      throw new IllegalArgumentException("Invalid account ID format: " + accountId);
    }
  }

  @Override
  @Transactional
  public void deleteBankAccountForUser(String accountId, String userEmail) {
    logger.info("Deleting bank account {} for user: {}", accountId, userEmail);
    
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id)
          .orElseThrow(() -> {
            logger.warn("Bank account not found with ID: {} for user: {}", accountId, userEmail);
            return new IllegalArgumentException("Bank account not found");
          });
      
      // Verify account belongs to the user
      if (!bankAccount.getUserId().getEmail().equals(userEmail)) {
        logger.warn("Unauthorized delete attempt: User {} tried to delete account {} owned by {}",
                    userEmail, accountId, bankAccount.getUserId().getEmail());
        throw new SecurityException("Access denied: Account does not belong to authenticated user");
      }
      
      // Check if account has transactions
      List<com.eaglebank.api.model.transaction.Transaction> sourceTransactions =
          transactionRepository.findBySourceAccountIdOrderByTimestampDesc(id);
      List<com.eaglebank.api.model.transaction.Transaction> destinationTransactions =
          transactionRepository.findByDestinationAccountIdOrderByTimestampDesc(id);
      
      if (!sourceTransactions.isEmpty() || !destinationTransactions.isEmpty()) {
        logger.warn("Cannot delete account {} with {} source and {} destination transactions",
                    accountId, sourceTransactions.size(), destinationTransactions.size());
        throw new IllegalStateException("Cannot delete account with existing transactions");
      }
      
      // Delete the account
      bankAccountRepository.delete(bankAccount);
      logger.info("Successfully deleted bank account: {} for user: {}",
                  bankAccount.getAccountNumber(), userEmail);
      
    } catch (NumberFormatException e) {
      logger.error("Invalid account ID format: {} for user: {}", accountId, userEmail);
      throw new IllegalArgumentException("Invalid account ID format: " + accountId);
    }
  }

}
