package com.eaglebank.api.controllers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.dto.error.BadRequestErrorResponse;
import com.eaglebank.api.dto.error.ErrorResponse;
import com.eaglebank.api.dto.transaction.CreateTransactionRequest;
import com.eaglebank.api.dto.transaction.ListTransactionsResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.service.transaction.TransactionService;

import jakarta.validation.Valid;

@RestController
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @PostMapping("/v1/accounts/{accountNumber}/transactions")
  public ResponseEntity<?> createTransaction(
      @PathVariable String accountNumber,
      @Valid @RequestBody CreateTransactionRequest request,
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
      TransactionResponse response = transactionService.createTransaction(accountNumber, request, userEmail);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
      
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse("The user is not allowed to access the transaction"));
    } catch (IllegalArgumentException e) {
      String message = e.getMessage();
      if (message.contains("Account not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Bank account was not found"));
      } else if (message.contains("Insufficient funds")) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse("Insufficient funds to process transaction"));
      } else {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("general", message, "validation");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @GetMapping("/v1/accounts/{accountNumber}/transactions")
  public ResponseEntity<?> getTransactions(
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
      ListTransactionsResponse response = transactionService.getTransactionsForAccount(accountNumber, userEmail);
      return ResponseEntity.ok(response);
      
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse("The user is not allowed to access the transactions"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ErrorResponse("Bank account was not found"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @GetMapping("/v1/accounts/{accountNumber}/transactions/{transactionId}")
  public ResponseEntity<?> getTransaction(
      @PathVariable String accountNumber,
      @PathVariable String transactionId,
      Authentication authentication) {
    try {
      // Validate account number format
      if (!accountNumber.matches("^01\\d{6}$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("accountNumber", "Invalid account number format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      // Validate transaction ID format
      if (!transactionId.matches("^tan-[A-Za-z0-9]+$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("transactionId", "Invalid transaction ID format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      String userEmail = authentication.getName();
      TransactionResponse response = transactionService.getTransactionById(accountNumber, transactionId, userEmail);
      return ResponseEntity.ok(response);
      
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse("The user is not allowed to access the transaction"));
    } catch (IllegalArgumentException e) {
      String message = e.getMessage();
      if (message.contains("Account not found") || message.contains("Transaction not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(message));
      } else {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("general", message, "validation");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }
}