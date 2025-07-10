package com.eaglebank.api.controllers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.account.CreateBankAccountRequest;
import com.eaglebank.api.dto.account.ListBankAccountsResponse;
import com.eaglebank.api.dto.account.UpdateBankAccountRequest;
import com.eaglebank.api.dto.error.BadRequestErrorResponse;
import com.eaglebank.api.dto.error.ErrorResponse;
import com.eaglebank.api.service.account.BankAccountService;

import jakarta.validation.Valid;

@RestController
public class BankAccountController {

  @Autowired
  private BankAccountService bankAccountService;

  @PostMapping("/v1/accounts")
  public ResponseEntity<?> createAccount(
      @Valid @RequestBody CreateBankAccountRequest request,
      Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      BankAccountResponse response = bankAccountService.createBankAccount(request, userEmail);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @GetMapping("/v1/accounts")
  public ResponseEntity<?> listAccounts(Authentication authentication) {
    try {
      String userEmail = authentication.getName();
      ListBankAccountsResponse response = bankAccountService.getBankAccountsForUser(userEmail);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @GetMapping("/v1/accounts/{accountNumber}")
  public ResponseEntity<?> getAccount(
      @PathVariable String accountNumber,
      Authentication authentication) {
    try {
      // Validate account number format
      if (!accountNumber.matches("^01\\d{6}$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("accountNumber", "Invalid account number format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      String userEmail = authentication.getName();
      BankAccountResponse response = bankAccountService.getBankAccountByAccountNumber(accountNumber, userEmail);
      return ResponseEntity.ok(response);
      
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse("The user is not allowed to access the bank account details"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ErrorResponse("Bank account was not found"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @PatchMapping("/v1/accounts/{accountNumber}")
  public ResponseEntity<?> updateAccount(
      @PathVariable String accountNumber,
      @Valid @RequestBody UpdateBankAccountRequest request,
      Authentication authentication) {
    try {
      // Validate account number format
      if (!accountNumber.matches("^01\\d{6}$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("accountNumber", "Invalid account number format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      String userEmail = authentication.getName();
      BankAccountResponse response = bankAccountService.updateBankAccount(accountNumber, request, userEmail);
      return ResponseEntity.ok(response);
      
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse("The user is not allowed to update the bank account details"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ErrorResponse("Bank account was not found"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @DeleteMapping("/v1/accounts/{accountNumber}")
  public ResponseEntity<?> deleteAccount(
      @PathVariable String accountNumber,
      Authentication authentication) {
    try {
      // Validate account number format
      if (!accountNumber.matches("^01\\d{6}$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("accountNumber", "Invalid account number format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      String userEmail = authentication.getName();
      bankAccountService.deleteBankAccount(accountNumber, userEmail);
      return ResponseEntity.noContent().build();
      
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse("The user is not allowed to delete the bank account details"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ErrorResponse("Bank account was not found"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }
}
