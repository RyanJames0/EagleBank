package com.eaglebank.api.model.account;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.eaglebank.api.model.transaction.Transaction;
import com.eaglebank.api.model.user.User;
@Entity
public class BankAccount { 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  private BankAccountType accountType;


  // For simplicity, I am avoiding the
  // 16 digit primary account number 
  // and pin data

  private String accountNumber;
  private String sortCode;
  private BigDecimal balance;
  
  @OneToMany(mappedBy = "sourceAccount")
  private List<Transaction> transactions;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public User getUserId() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
  
  public BankAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(BankAccountType string) {
    this.accountType = string;
  }
}
