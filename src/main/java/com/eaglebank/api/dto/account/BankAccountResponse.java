package com.eaglebank.api.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eaglebank.api.model.account.BankAccount;
import com.eaglebank.api.model.account.BankAccountType;

public class BankAccountResponse {
  private long id;
  private String accountNumber;
  private String sortCode;
  private BigDecimal balance;
  private BankAccountType accountType;
  private LocalDate expiryDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BankAccountResponse(BankAccount bankAccount) {
    this.id = bankAccount.getId();
    this.accountNumber = bankAccount.getAccountNumber();
    this.sortCode = bankAccount.getSortCode();
    this.balance = bankAccount.getBalance();
    this.accountType = bankAccount.getAccountType();
    this.expiryDate = bankAccount.getExpiryDate();
    this.createdAt = bankAccount.getCreatedAt();
    this.updatedAt = bankAccount.getUpdatedAt();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public BankAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(BankAccountType accountType) {
    this.accountType = accountType;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
