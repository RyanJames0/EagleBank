package com.eaglebank.api.controllers;

import java.util.Arrays;
import java.util.List;

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

import com.eaglebank.api.dto.error.BadRequestErrorResponse;
import com.eaglebank.api.dto.error.ErrorResponse;
import com.eaglebank.api.dto.user.CreateUserRequest;
import com.eaglebank.api.dto.user.UpdateUserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.service.user.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/v1/users")
  public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
    try {
      UserResponse response = userService.createUser(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @GetMapping("/v1/users/{userId}")
  public ResponseEntity<?> getUserById(
      @PathVariable String userId,
      Authentication authentication) {
    try {
      // Validate userId format
      if (!userId.matches("^usr-[A-Za-z0-9]+$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("userId", "Invalid user ID format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      // Get authenticated user
      String authenticatedEmail = authentication.getName();
      User authenticatedUser = userService.getUserByEmail(authenticatedEmail);
      
      if (authenticatedUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("User authentication required"));
      }

      // Check if user is trying to access their own data
      if (!authenticatedUser.getId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("Access denied: You can only view your own user details"));
      }

      UserResponse response = userService.getUserById(userId);
      if (response == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("User not found"));
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @PatchMapping("/v1/users/{userId}")
  public ResponseEntity<?> updateUser(
      @PathVariable String userId,
      @Valid @RequestBody UpdateUserRequest request,
      Authentication authentication) {
    try {
      // Validate userId format
      if (!userId.matches("^usr-[A-Za-z0-9]+$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("userId", "Invalid user ID format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      // Get authenticated user
      String authenticatedEmail = authentication.getName();
      User authenticatedUser = userService.getUserByEmail(authenticatedEmail);
      
      if (authenticatedUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("User authentication required"));
      }

      // Check if user is trying to update their own data
      if (!authenticatedUser.getId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("Access denied: You can only update your own user details"));
      }

      UserResponse response = userService.updateUser(userId, request);
      if (response == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("User not found"));
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }

  @DeleteMapping("/v1/users/{userId}")
  public ResponseEntity<?> deleteUser(
      @PathVariable String userId,
      Authentication authentication) {
    try {
      // Validate userId format
      if (!userId.matches("^usr-[A-Za-z0-9]+$")) {
        BadRequestErrorResponse.ValidationError validationError =
            new BadRequestErrorResponse.ValidationError("userId", "Invalid user ID format", "format");
        return ResponseEntity.badRequest()
            .body(new BadRequestErrorResponse("Validation failed", Arrays.asList(validationError)));
      }

      // Get authenticated user
      String authenticatedEmail = authentication.getName();
      User authenticatedUser = userService.getUserByEmail(authenticatedEmail);
      
      if (authenticatedUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("User authentication required"));
      }

      // Check if user is trying to delete their own data
      if (!authenticatedUser.getId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("Access denied: You can only delete your own user account"));
      }

      userService.deleteUser(userId);
      return ResponseEntity.noContent().build();
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ErrorResponse("User not found"));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse("An unexpected error occurred"));
    }
  }
}
