package com.eaglebank.api.dto.transaction;

import java.math.BigDecimal;

import com.eaglebank.api.model.transaction.TransactionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateTransactionRequest {

  @DecimalMin(value = "0.00")
  @DecimalMax(value = "10000.00")
  @NotNull
  private BigDecimal amount;

  @NotBlank
  @Pattern(regexp = "^GBP$", message = "Currency must be GBP")
  private String currency = "GBP";

  @NotNull
  private TransactionType type;

  private String reference;

  public CreateTransactionRequest() {
  }

  public CreateTransactionRequest(BigDecimal amount, String currency, TransactionType type, String reference) {
    this.amount = amount;
    this.currency = currency;
    this.type = type;
    this.reference = reference;
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
}