package com.eaglebank.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.dto.account.BankAccountRequest;

import com.eaglebank.api.model.transaction.Transaction;

@RestController
public class BankAccountController {
 
  @PostMapping("/v1/accounts")
  public ResponseEntity<?> createAccount(@RequestBody BankAccountRequest accountRequest) {
    // Logic to create a bank account
    // Validate input, save to database, etc.
    
    // Assuming the account creation is successful
    return ResponseEntity.status(HttpStatus.CREATED).body("Bank account created successfully");
  }

}
