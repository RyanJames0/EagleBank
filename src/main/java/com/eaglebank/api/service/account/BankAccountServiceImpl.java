package com.eaglebank.api.service.account;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eaglebank.api.dto.account.BankAccountRequest;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.service.user.UserService;

@Service
public class BankAccountServiceImpl implements BankAccountService {
  @Autowired private BankAccountRepository bankAccountRepository;
  @Autowired private UserService userService;

  @Override
  public BankAccountResponse createBankAccount(BankAccountRequest accountRequest) {
    BankAccount bankAccount = new BankAccount();
    // Set some hardcoded values for demonstration purposes
    //
    bankAccount.setAccountNumber("123456789");
    bankAccount.setAccountType(accountRequest.getAccountType());
    bankAccount.setBalance(BigDecimal.ZERO);
    bankAccount.setSortCode("00-00-00");

    try {
      User user = userService.getUserByEmail(
        accountRequest.getEmail());
      bankAccount.setUser(user);
      bankAccountRepository.save(bankAccount);
      return new BankAccountResponse(bankAccount);
    } catch (Exception e) {
      // Handle exception
      return null; 
    }
  }

  @Override
  public BankAccountResponse getBankAccountById(String accountId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BankAccountResponse updateBankAccount(String accountId, BankAccountRequest accountRequest) {
    // TODO Auto-generated method stub
    return null;
  }

}
