package com.eaglebank.api.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @Pattern(regexp = "^tan-[A-Za-z0-9]+$")
  private String id;

  @DecimalMin(value = "0.00")
  @DecimalMax(value = "10000.00")
  @NotNull
  private BigDecimal amount;

  @NotBlank
  private String currency = "GBP";

  @Enumerated(EnumType.STRING)
  @NotNull
  private TransactionType type;

  private String reference;

  @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
  @NotBlank
  private String userId;

  @Pattern(regexp = "^01\\d{6}$")
  @NotBlank
  private String accountNumber;

  private LocalDateTime createdTimestamp;

  public Transaction() {
  }

  public Transaction(String id, BigDecimal amount, String currency, TransactionType type, 
                    String reference, String userId, String accountNumber) {
    this.id = id;
    this.amount = amount;
    this.currency = currency;
    this.type = type;
    this.reference = reference;
    this.userId = userId;
    this.accountNumber = accountNumber;
  }

  @PrePersist
  protected void onCreate() {
    createdTimestamp = LocalDateTime.now();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public LocalDateTime getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }
}
