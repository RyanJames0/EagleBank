package com.eaglebank.api.dto.transaction;

import java.math.BigDecimal;

import com.eaglebank.api.model.transaction.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransactionRequest {

  @NotNull(message = "Source account ID is required")
  private Long sourceAccountId;
  
  private Long destinationAccountId; // Optional for deposits/withdrawals
  
  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
  private BigDecimal amount;
  
  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;
  
  @NotNull(message = "Transaction type is required")
  private TransactionType type;

  public Long getSourceAccountId() {
    return sourceAccountId;
  }

  public void setSourceAccountId(final Long sourceAccountId) {
    this.sourceAccountId = sourceAccountId;
  }

  public Long getDestinationAccountId() {
    return destinationAccountId;
  }

  public void setDestinationAccountId(final Long destinationAccountId) {
    this.destinationAccountId = destinationAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(final TransactionType type) {
    this.type = type;
  }
}