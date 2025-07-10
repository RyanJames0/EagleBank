package com.eaglebank.api.dto.account;

import com.eaglebank.api.model.account.BankAccountType;

public class BankAccountRequest {
  private String email; 
  private BankAccountType accountType; 

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public BankAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(BankAccountType accountType) {
    this.accountType = accountType;
  }
}
