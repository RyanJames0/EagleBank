package com.eaglebank.api.dto.account;

import com.eaglebank.api.model.account.BankAccountType;

public class UpdateBankAccountRequest {

  private String name;
  private BankAccountType accountType;

  public UpdateBankAccountRequest() {
  }

  public UpdateBankAccountRequest(String name, BankAccountType accountType) {
    this.name = name;
    this.accountType = accountType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BankAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(BankAccountType accountType) {
    this.accountType = accountType;
  }
}