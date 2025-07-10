package com.eaglebank.api.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eaglebank.api.model.transaction.TransactionType;

public class TransactionResponse {

  private Long id;
  private TransactionType type;
  private BigDecimal amount;
  private LocalDateTime timestamp;
  private Long accountId;
  private BigDecimal newBalance;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(final TransactionType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(final Long accountId) {
    this.accountId = accountId;
  }

  public BigDecimal getNewBalance() {
    return newBalance;
  }

  public void setNewBalance(final BigDecimal newBalance) {
    this.newBalance = newBalance;
  }
}