package com.eaglebank.api.model.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

  @Id
  @Pattern(regexp = "^01\\d{6}$")
  private String accountNumber;

  @NotBlank
  private String sortCode = "10-10-10";

  @NotBlank
  private String name;

  @Enumerated(EnumType.STRING)
  @NotNull
  private BankAccountType accountType;

  @DecimalMin(value = "0.00")
  @DecimalMax(value = "10000.00")
  @NotNull
  private BigDecimal balance = BigDecimal.ZERO;

  @NotBlank
  private String currency = "GBP";

  @NotBlank
  private String userEmail;

  private LocalDateTime createdTimestamp;

  private LocalDateTime updatedTimestamp;

  public BankAccount() {
  }

  public BankAccount(String accountNumber, String name, BankAccountType accountType, String userEmail) {
    this.accountNumber = accountNumber;
    this.name = name;
    this.accountType = accountType;
    this.userEmail = userEmail;
    this.balance = BigDecimal.ZERO;
    this.currency = "GBP";
    this.sortCode = "10-10-10";
  }

  @PrePersist
  protected void onCreate() {
    createdTimestamp = LocalDateTime.now();
    updatedTimestamp = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedTimestamp = LocalDateTime.now();
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

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
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
