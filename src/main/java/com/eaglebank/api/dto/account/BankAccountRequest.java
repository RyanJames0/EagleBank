package com.eaglebank.api.dto.account;

import com.eaglebank.api.model.account.BankAccountType;

public class BankAccountRequest {
  private String userEmail; 
  private BankAccountType accountType; 

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserId(String userId) {
    this.userEmail = userId;
  }

  public BankAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(BankAccountType accountType) {
    this.accountType = accountType;
  }
}
