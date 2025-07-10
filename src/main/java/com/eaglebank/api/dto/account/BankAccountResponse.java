package com.eaglebank.api.dto.account;

import java.math.BigDecimal;

import com.eaglebank.api.model.account.BankAccount;

public class BankAccountResponse {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;

    public BankAccountResponse(BankAccount bankAccount) {
        this.accountNumber = bankAccount.getAccountNumber();
        this.accountType = bankAccount.getAccountType().name();
        this.balance = bankAccount.getBalance();
    }
    
    // Getters and setters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
}
