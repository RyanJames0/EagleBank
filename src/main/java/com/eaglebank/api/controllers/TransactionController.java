package com.eaglebank.api.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.dto.transaction.TransactionRequest;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.model.ApiResponse;
import com.eaglebank.api.service.transaction.TransactionService;
import com.eaglebank.api.utils.InputValidation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
public class TransactionController {
  private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  private TransactionService transactionService;

  @PostMapping("/{accountId}/transactions")
  public ResponseEntity<ApiResponse> createTransaction(
      @PathVariable final Long accountId,
      @Valid @RequestBody final TransactionRequest transactionRequest,
      final Authentication authentication) {
    
    logger.info("Creating transaction for account: {} by user: {}", accountId, authentication.getName());
    
    try {
      // Validate account ID
      if (accountId == null || accountId <= 0) {
        logger.warn("Invalid account ID provided: {}", accountId);
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Valid account ID is required", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        logger.warn("Invalid user authentication for transaction creation");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      TransactionResponse response = transactionService.createTransaction(accountId, transactionRequest, userEmail);
      
      logger.info("Successfully created transaction: {} for account: {} by user: {}",
                  response.getId(), accountId, userEmail);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse(true, "Transaction created successfully", response));

    } catch (IllegalArgumentException e) {
      logger.warn("Transaction creation failed for account: {} by user: {} - {}",
                  accountId, authentication.getName(), e.getMessage());
      
      String message = e.getMessage();
      
      if (message.contains("Account not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse(false, message, null));
      } else if (message.contains("Account does not belong to authenticated user")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse(false, message, null));
      } else if (message.contains("Insufficient funds")) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiResponse(false, message, null));
      } else {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, message, null));
      }
    } catch (SecurityException e) {
      logger.warn("Security violation during transaction creation for account: {} by user: {} - {}",
                  accountId, authentication.getName(), e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      logger.error("Unexpected error creating transaction for account: {} by user: {}",
                   accountId, authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }

  @GetMapping("/{accountId}/transactions")
  public ResponseEntity<ApiResponse> getTransactions(
      @PathVariable final Long accountId,
      final Authentication authentication) {
    
    logger.debug("Retrieving transactions for account: {} by user: {}", accountId, authentication.getName());
    
    try {
      // Validate account ID
      if (accountId == null || accountId <= 0) {
        logger.warn("Invalid account ID provided: {}", accountId);
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Valid account ID is required", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        logger.warn("Invalid user authentication for transaction retrieval");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      List<TransactionResponse> transactions = transactionService.getTransactionsForAccount(accountId, userEmail);
      
      logger.debug("Successfully retrieved {} transactions for account: {} by user: {}",
                   transactions.size(), accountId, userEmail);

      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Transactions retrieved successfully", transactions));

    } catch (IllegalArgumentException e) {
      logger.warn("Failed to retrieve transactions for account: {} by user: {} - {}",
                  accountId, authentication.getName(), e.getMessage());
      
      String message = e.getMessage();
      
      if (message.contains("Account not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse(false, message, null));
      } else if (message.contains("Account does not belong to authenticated user")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse(false, message, null));
      } else {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, message, null));
      }
    } catch (SecurityException e) {
      logger.warn("Security violation during transaction retrieval for account: {} by user: {} - {}",
                  accountId, authentication.getName(), e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      logger.error("Unexpected error retrieving transactions for account: {} by user: {}",
                   accountId, authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }

  @GetMapping("/{accountId}/transactions/{transactionId}")
  public ResponseEntity<ApiResponse> getTransaction(
      @PathVariable final Long accountId,
      @PathVariable final Long transactionId,
      final Authentication authentication) {
    
    logger.debug("Retrieving transaction: {} for account: {} by user: {}",
                 transactionId, accountId, authentication.getName());
    
    try {
      // Validate account ID
      if (accountId == null || accountId <= 0) {
        logger.warn("Invalid account ID provided: {}", accountId);
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Valid account ID is required", null));
      }

      // Validate transaction ID
      if (transactionId == null || transactionId <= 0) {
        logger.warn("Invalid transaction ID provided: {}", transactionId);
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Valid transaction ID is required", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        logger.warn("Invalid user authentication for transaction retrieval");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      TransactionResponse transaction = transactionService.getTransactionById(accountId, transactionId, userEmail);
      
      logger.debug("Successfully retrieved transaction: {} for account: {} by user: {}",
                   transactionId, accountId, userEmail);

      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Transaction retrieved successfully", transaction));

    } catch (IllegalArgumentException e) {
      logger.warn("Failed to retrieve transaction: {} for account: {} by user: {} - {}",
                  transactionId, accountId, authentication.getName(), e.getMessage());
      
      String message = e.getMessage();
      
      if (message.contains("Account not found") || message.contains("Transaction not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse(false, message, null));
      } else if (message.contains("Account does not belong to authenticated user")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse(false, message, null));
      } else {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, message, null));
      }
    } catch (SecurityException e) {
      logger.warn("Security violation during transaction retrieval: {} for account: {} by user: {} - {}",
                  transactionId, accountId, authentication.getName(), e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      logger.error("Unexpected error retrieving transaction: {} for account: {} by user: {}",
                   transactionId, accountId, authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }
}