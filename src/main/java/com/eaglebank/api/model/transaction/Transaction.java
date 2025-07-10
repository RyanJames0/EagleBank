package com.eaglebank.api.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.eaglebank.api.model.account.BankAccount;

@Entity
@Table(name = "transaction", indexes = {
    @Index(name = "idx_transaction_source_account", columnList = "sourceAccount_id"),
    @Index(name = "idx_transaction_destination_account", columnList = "destinationAccount_id"),
    @Index(name = "idx_transaction_timestamp", columnList = "timestamp"),
    @Index(name = "idx_transaction_type", columnList = "type"),
    @Index(name = "idx_transaction_source_timestamp", columnList = "sourceAccount_id, timestamp"),
    @Index(name = "idx_transaction_dest_timestamp", columnList = "destinationAccount_id, timestamp")
})
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
