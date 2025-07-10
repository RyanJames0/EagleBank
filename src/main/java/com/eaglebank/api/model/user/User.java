package com.eaglebank.api.model.user;

import java.util.Collections;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

import com.eaglebank.api.model.account.BankAccount;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    
    @OneToMany(mappedBy = "user")
    private List<BankAccount> bankAccounts; 

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPasswordHash() { return passwordHash; }
    public List<BankAccount> getBankAccounts() { return Collections.unmodifiableList(bankAccounts); }
    public void addBankAccount(BankAccount account) {
        account.setUser(this);
        bankAccounts.add(account);
    }
}
