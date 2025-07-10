package com.eaglebank.api.dto.account;

import com.eaglebank.api.model.account.BankAccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BankAccountRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;
  
  @NotNull(message = "Account type is required")
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
