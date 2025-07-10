package com.eaglebank.api.dto.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.model.account.BankAccountType;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BankAccountResponse {

  private String accountNumber;
  private String sortCode;
  private String name;
  private BankAccountType accountType;
  private BigDecimal balance;
  private String currency;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdTimestamp;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime updatedTimestamp;

  public BankAccountResponse() {
  }

  public BankAccountResponse(BankAccount bankAccount) {
    this.accountNumber = bankAccount.getAccountNumber();
    this.sortCode = bankAccount.getSortCode();
    this.name = bankAccount.getName();
    this.accountType = bankAccount.getAccountType();
    this.balance = bankAccount.getBalance();
    this.currency = bankAccount.getCurrency();
    this.createdTimestamp = bankAccount.getCreatedTimestamp();
    this.updatedTimestamp = bankAccount.getUpdatedTimestamp();
  }

  public BankAccountResponse(String accountNumber, String sortCode, String name, 
                           BankAccountType accountType, BigDecimal balance, String currency,
                           LocalDateTime createdTimestamp, LocalDateTime updatedTimestamp) {
    this.accountNumber = accountNumber;
    this.sortCode = sortCode;
    this.name = name;
    this.accountType = accountType;
    this.balance = balance;
    this.currency = currency;
    this.createdTimestamp = createdTimestamp;
    this.updatedTimestamp = updatedTimestamp;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getSortCode() {
    return sortCode;
  }

  public void setSortCode(String sortCode) {
    this.sortCode = sortCode;
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

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public LocalDateTime getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public LocalDateTime getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
  }
}
