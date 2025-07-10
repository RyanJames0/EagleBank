package com.eaglebank.api.service.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.account.CreateBankAccountRequest;
import com.eaglebank.api.dto.account.ListBankAccountsResponse;
import com.eaglebank.api.dto.account.UpdateBankAccountRequest;
import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;

@Service
public class BankAccountServiceImpl implements BankAccountService {

  @Autowired private BankAccountRepository bankAccountRepository;
  @Autowired private TransactionRepository transactionRepository;

  @Override
  public BankAccountResponse createBankAccount(CreateBankAccountRequest request, String userEmail) {
    // Generate account number in format 01XXXXXX
    String accountNumber = generateAccountNumber();
    
    BankAccount bankAccount = new BankAccount();
    bankAccount.setAccountNumber(accountNumber);
    bankAccount.setName(request.getName());
    bankAccount.setAccountType(request.getAccountType());
    bankAccount.setUserEmail(userEmail);
    bankAccount.setBalance(BigDecimal.ZERO);
    bankAccount.setCurrency("GBP");
    bankAccount.setSortCode("10-10-10");
    
    BankAccount savedAccount = bankAccountRepository.save(bankAccount);
    return new BankAccountResponse(savedAccount);
  }

  @Override
  public ListBankAccountsResponse getBankAccountsForUser(String userEmail) {
    List<BankAccount> accounts = bankAccountRepository.findByUserEmail(userEmail);
    List<BankAccountResponse> accountResponses = accounts.stream()
        .map(BankAccountResponse::new)
        .collect(Collectors.toList());
    
    return new ListBankAccountsResponse(accountResponses);
  }

  @Override
  public BankAccountResponse getBankAccountByAccountNumber(String accountNumber, String userEmail) {
    Optional<BankAccount> account = bankAccountRepository.findByAccountNumberAndUserEmail(accountNumber, userEmail);
    
    if (account.isEmpty()) {
      // Check if account exists but belongs to different user
      if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
        throw new SecurityException("Account does not belong to authenticated user");
      } else {
        throw new IllegalArgumentException("Account not found");
      }
    }
    
    return new BankAccountResponse(account.get());
  }

  @Override
  public BankAccountResponse updateBankAccount(String accountNumber, UpdateBankAccountRequest request, String userEmail) {
    Optional<BankAccount> accountOpt = bankAccountRepository.findByAccountNumberAndUserEmail(accountNumber, userEmail);
    
    if (accountOpt.isEmpty()) {
      // Check if account exists but belongs to different user
      if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
        throw new SecurityException("Account does not belong to authenticated user");
      } else {
        throw new IllegalArgumentException("Account not found");
      }
    }
    
    BankAccount account = accountOpt.get();
    
    if (request.getName() != null) {
      account.setName(request.getName());
    }
    if (request.getAccountType() != null) {
      account.setAccountType(request.getAccountType());
    }
    
    BankAccount savedAccount = bankAccountRepository.save(account);
    return new BankAccountResponse(savedAccount);
  }

  @Override
  public void deleteBankAccount(String accountNumber, String userEmail) {
    Optional<BankAccount> accountOpt = bankAccountRepository.findByAccountNumberAndUserEmail(accountNumber, userEmail);
    
    if (accountOpt.isEmpty()) {
      // Check if account exists but belongs to different user
      if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
        throw new SecurityException("Account does not belong to authenticated user");
      } else {
        throw new IllegalArgumentException("Account not found");
      }
    }
    
    // Check if account has transactions
    boolean hasTransactions = transactionRepository.existsByAccountNumber(accountNumber);
    if (hasTransactions) {
      throw new IllegalStateException("Cannot delete account with existing transactions");
    }
    
    bankAccountRepository.delete(accountOpt.get());
  }

  private String generateAccountNumber() {
    Random random = new Random();
    String accountNumber;
    
    do {
      // Generate 6 random digits after "01"
      int randomNumber = 100000 + random.nextInt(900000);
      accountNumber = "01" + randomNumber;
    } while (bankAccountRepository.existsByAccountNumber(accountNumber));
    
    return accountNumber;
  }
}
