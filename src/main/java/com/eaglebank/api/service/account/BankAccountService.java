package com.eaglebank.api.service.account;

import java.util.List;

import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.account.CreateBankAccountRequest;
import com.eaglebank.api.dto.account.ListBankAccountsResponse;
import com.eaglebank.api.dto.account.UpdateBankAccountRequest;

public interface BankAccountService {

  BankAccountResponse createBankAccount(CreateBankAccountRequest request, String userEmail);
  
  ListBankAccountsResponse getBankAccountsForUser(String userEmail);
  
  BankAccountResponse getBankAccountByAccountNumber(String accountNumber, String userEmail);
  
  BankAccountResponse updateBankAccount(String accountNumber, UpdateBankAccountRequest request, String userEmail);
  
  void deleteBankAccount(String accountNumber, String userEmail);
}
