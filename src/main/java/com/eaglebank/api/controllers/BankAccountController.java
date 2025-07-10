package com.eaglebank.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.dto.account.BankAccountRequest;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.model.ApiResponse;
import com.eaglebank.api.service.account.BankAccountService;
import com.eaglebank.api.utils.InputValidation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
public class BankAccountController {

  @Autowired
  private BankAccountService bankAccountService;

  @PostMapping
  public ResponseEntity<ApiResponse> createAccount(@Valid @RequestBody final BankAccountRequest accountRequest) {
    try {
      // Validate input
      if (InputValidation.isInvalidInput(accountRequest.getEmail())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "User email is required", null));
      }

      if (accountRequest.getAccountType() == null) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Account type is required", null));
      }

      BankAccountResponse response = bankAccountService.createBankAccount(accountRequest);
      
      if (response == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse(false, "Failed to create bank account", null));
      }

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse(true, "Bank account created successfully", response));

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }

  @GetMapping
  public ResponseEntity<ApiResponse> getAllAccounts(final Authentication authentication) {
    try {
      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      List<BankAccountResponse> accounts = bankAccountService.getBankAccountsForUser(userEmail);

      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Bank accounts retrieved successfully", accounts));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }

  @GetMapping("/{accountId}")
  public ResponseEntity<ApiResponse> getAccount(
      @PathVariable final String accountId,
      final Authentication authentication) {
    try {
      if (accountId == null || accountId.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Account ID is required", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      BankAccountResponse response = bankAccountService.getBankAccountByIdForUser(accountId, userEmail);
      
      if (response == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse(false, "Bank account not found", null));
      }

      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Bank account retrieved successfully", response));

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }

  @PatchMapping("/{accountId}")
  public ResponseEntity<ApiResponse> updateAccount(
      @PathVariable final String accountId,
      @Valid @RequestBody final BankAccountRequest accountRequest,
      final Authentication authentication) {
    try {
      if (accountId == null || accountId.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Account ID is required", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      // Validate account type if provided (PATCH allows partial updates)
      if (accountRequest.getAccountType() == null) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Account type is required for update", null));
      }

      BankAccountResponse response = bankAccountService.updateBankAccountForUser(accountId, accountRequest, userEmail);
      
      if (response == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse(false, "Bank account not found", null));
      }

      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Bank account updated successfully", response));

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }

  @DeleteMapping("/{accountId}")
  public ResponseEntity<ApiResponse> deleteAccount(
      @PathVariable final String accountId,
      final Authentication authentication) {
    try {
      if (accountId == null || accountId.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Account ID is required", null));
      }

      // Get authenticated user email
      String userEmail = authentication.getName();
      if (InputValidation.isInvalidInput(userEmail)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "User authentication required", null));
      }

      bankAccountService.deleteBankAccountForUser(accountId, userEmail);

      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Bank account deleted successfully", null));

    } catch (IllegalArgumentException e) {
      String message = e.getMessage();
      
      if (message.contains("Account not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse(false, message, null));
      } else if (message.contains("Account does not belong to authenticated user")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse(false, message, null));
      } else if (message.contains("Cannot delete account with existing transactions")) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiResponse(false, message, null));
      } else {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, message, null));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }
}
