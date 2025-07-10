package com.eaglebank.api.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import com.eaglebank.api.model.account.BankAccount;
@Entity
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private BankAccount sourceAccount;

  @ManyToOne
  @Nullable
  private BankAccount destinationAccount; 
  private BigDecimal amount;
  private TransactionType type; 

  private LocalDateTime timestamp;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BankAccount getSourceAccount() {
    return sourceAccount;
  }

  public void setSourceAccount(BankAccount sourceAccount) {
    this.sourceAccount = sourceAccount;
  }

  public BankAccount getDestinationAccount() {
    return destinationAccount;
  }

  public void setDestinationAccount(BankAccount destinationAccount) {
    this.destinationAccount = destinationAccount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
