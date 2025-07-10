package com.eaglebank.api.dto.transaction;

import java.util.List;

public class ListTransactionsResponse {

  private List<TransactionResponse> transactions;

  public ListTransactionsResponse() {
  }

  public ListTransactionsResponse(List<TransactionResponse> transactions) {
    this.transactions = transactions;
  }

  public List<TransactionResponse> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionResponse> transactions) {
    this.transactions = transactions;
  }
}