package com.eaglebank.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

  @Autowired
  private TransactionService transactionService;

  @PostMapping("/{accountId}/transactions")
  public ResponseEntity<ApiResponse> createTransaction(
      @PathVariable final Long accountId,
      @Valid @RequestBody final TransactionRequest transactionRequest,
      final Authentication authentication) {
    
    try {
      // Validate account ID
      if (accountId == null || accountId <= 0) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Valid account ID is required", null));
      }

      // Validate transaction type
      if (transactionRequest.getType() == null) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Transaction type is required", null));
      }

      // Validate amount
      if (transactionRequest.getAmount() == null || transactionRequest.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Amount must be greater than 0", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      TransactionResponse response = transactionService.createTransaction(accountId, transactionRequest, userEmail);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse(true, "Transaction created successfully", response));

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }
}