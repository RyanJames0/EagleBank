package com.eaglebank.api.service.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
  @Autowired private BankAccountRepository bankAccountRepository;
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private UserService userService;

  @Override
  public BankAccountResponse createBankAccount(BankAccountRequest accountRequest) {
    BankAccount bankAccount = new BankAccount();
    
    // Generate sequential account number
    String accountNumber = generateSequentialAccountNumber();
    bankAccount.setAccountNumber(accountNumber);
    bankAccount.setAccountType(accountRequest.getAccountType());
    bankAccount.setBalance(BigDecimal.ZERO);
    bankAccount.setSortCode("40-47-84"); // Eagle Bank sort code
    
    // Set expiry date (5 years from now for debit cards)
    bankAccount.setExpiryDate(LocalDate.now().plusYears(5));

    try {
      User user = userService.getUserByEmail(accountRequest.getEmail());
      bankAccount.setUser(user);
      bankAccountRepository.save(bankAccount);
      return new BankAccountResponse(bankAccount);
    } catch (Exception e) {
      // Handle exception
      return null;
    }
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
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
      
      if (bankAccount == null) {
        return null;
      }
      
      return new BankAccountResponse(bankAccount);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid account ID format");
    }
  }

  @Override
  public BankAccountResponse getBankAccountByIdForUser(String accountId, String userEmail) {
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
      
      if (bankAccount == null) {
        return null;
      }
      
      // Verify account belongs to the user
      if (!bankAccount.getUserId().getEmail().equals(userEmail)) {
        throw new IllegalArgumentException("Account does not belong to authenticated user");
      }
      
      return new BankAccountResponse(bankAccount);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid account ID format");
    }
  }

  @Override
  public List<BankAccountResponse> getBankAccountsForUser(String userEmail) {
    List<BankAccount> bankAccounts = bankAccountRepository.findByUserEmail(userEmail);
    
    return bankAccounts.stream()
        .map(BankAccountResponse::new)
        .collect(Collectors.toList());
  }

  @Override
  public BankAccountResponse updateBankAccountForUser(String accountId, BankAccountRequest accountRequest, String userEmail) {
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
      
      if (bankAccount == null) {
        return null;
      }
      
      // Verify account belongs to the user
      if (!bankAccount.getUserId().getEmail().equals(userEmail)) {
        throw new IllegalArgumentException("Account does not belong to authenticated user");
      }
      
      // Update account type if provided
      if (accountRequest.getAccountType() != null) {
        bankAccount.setAccountType(accountRequest.getAccountType());
      }
      
      // Note: We don't update email as that would change ownership
      // Balance should only be updated through transactions, not direct updates
      
      bankAccount = bankAccountRepository.save(bankAccount);
      return new BankAccountResponse(bankAccount);
      
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid account ID format");
    }
  }

  @Override
  @Transactional
  public void deleteBankAccountForUser(String accountId, String userEmail) {
    try {
      Long id = Long.parseLong(accountId);
      BankAccount bankAccount = bankAccountRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Account not found"));
      
      // Verify account belongs to the user
      if (!bankAccount.getUserId().getEmail().equals(userEmail)) {
        throw new IllegalArgumentException("Account does not belong to authenticated user");
      }
      
      // Check if account has transactions
      List<com.eaglebank.api.model.transaction.Transaction> sourceTransactions =
          transactionRepository.findBySourceAccountIdOrderByTimestampDesc(id);
      List<com.eaglebank.api.model.transaction.Transaction> destinationTransactions =
          transactionRepository.findByDestinationAccountIdOrderByTimestampDesc(id);
      
      if (!sourceTransactions.isEmpty() || !destinationTransactions.isEmpty()) {
        throw new IllegalArgumentException("Cannot delete account with existing transactions");
      }
      
      // Delete the account
      bankAccountRepository.delete(bankAccount);
      
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid account ID format");
    }
  }

}
