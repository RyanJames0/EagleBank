package com.eaglebank.api.dto.account;

import com.eaglebank.api.model.account.BankAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBankAccountRequest {

  @NotBlank
  private String name;

  @NotNull
  private BankAccountType accountType;

  public CreateBankAccountRequest() {
  }

  public CreateBankAccountRequest(String name, BankAccountType accountType) {
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