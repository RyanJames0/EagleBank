package com.eaglebank.api.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eaglebank.api.model.transaction.Transaction;
import com.eaglebank.api.model.transaction.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TransactionResponse {

  private String id;
  private BigDecimal amount;
  private String currency;
  private TransactionType type;
  private String reference;
  private String userId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdTimestamp;

  public TransactionResponse() {
  }

  public TransactionResponse(Transaction transaction) {
    this.id = transaction.getId();
    this.amount = transaction.getAmount();
    this.currency = transaction.getCurrency();
    this.type = transaction.getType();
    this.reference = transaction.getReference();
    this.userId = transaction.getUserId();
    this.createdTimestamp = transaction.getCreatedTimestamp();
  }

  public TransactionResponse(String id, BigDecimal amount, String currency, TransactionType type,
                           String reference, String userId, LocalDateTime createdTimestamp) {
    this.id = id;
    this.amount = amount;
    this.currency = currency;
    this.type = type;
    this.reference = reference;
    this.userId = userId;
    this.createdTimestamp = createdTimestamp;
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

  public LocalDateTime getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }
}