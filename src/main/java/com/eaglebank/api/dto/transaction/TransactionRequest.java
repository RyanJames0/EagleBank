package com.eaglebank.api.dto.transaction;

import java.math.BigDecimal;

import com.eaglebank.api.model.transaction.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TransactionRequest {

  @NotNull(message = "Transaction type is required")
  private TransactionType type;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private BigDecimal amount;

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
}