package com.eaglebank.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.eaglebank.api.dto.user.UserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.model.ApiResponse;
import com.eaglebank.api.service.user.UserService;
import com.eaglebank.api.utils.InputValidation;

@RestController
public class UserController {
  @Autowired
  private UserService userService;

  private ResponseEntity<ApiResponse> createErrorResponse(
    HttpStatus status, String message) {
    return ResponseEntity
      .status(status)
      .body(new ApiResponse(false, message, null));
  }
  
  @PostMapping("/v1/users")
  public ResponseEntity<ApiResponse> createUser(@RequestBody UserRequest user) {
    if (InputValidation.isInvalidInput(user.getName())) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "Name is required");
    }

    if (InputValidation.isInvalidInput(user.getEmail())) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "Email is required");
    }

    if (InputValidation.isInvalidInput(user.getPassword())) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "Password is required");
    }

    UserResponse userResponse = userService.createUser(user); 
    if (userResponse == null) {
      return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, 
        "Failed to create user");
    }

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(new ApiResponse(
      true, 
      "User " + userResponse.getName() + " created successfully", 
      userResponse)); 
  }


  @GetMapping("/v1/users/{userId}")
  public ResponseEntity<ApiResponse> getUser(@PathVariable long userId) {
    if (userId < 0) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "User ID should be a positive number");
    }
   
    UserResponse userResponse = userService.getUserById(userId);

    if (userResponse == null) {
      return createErrorResponse(
        HttpStatus.NOT_FOUND, 
        "User not found");
    }
    
    return ResponseEntity
      .status(HttpStatus.OK)
      .body(new ApiResponse(
        true, 
        "User retrieved successfully", 
        userResponse));
  }

  @PatchMapping("/v1/users/{userId}") 
  public ResponseEntity<ApiResponse> updateUser(
    @PathVariable long userId, 
    @RequestBody UserRequest user) {
    
    if (userId < 0) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "User ID should be a positive number");
    }

    if (InputValidation.InvalidInput(user.getName())) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "Name is required");
    }

    if (InputValidation.isInvalidInput(user.getEmail())) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "Email is required");
    }

    if (InputValidation.isInvalidInput(user.getPassword())) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "Password is required");
    }

    UserResponse userResponse = userService.updateUser(userId, user);

    if (userResponse == null) {
      return createErrorResponse(
        HttpStatus.NOT_FOUND, 
        "User not found");
    }

    return ResponseEntity
      .status(HttpStatus.OK)
      .body(new ApiResponse(
        true, 
        "User updated successfully", 
        userResponse));
  }

  @DeleteMapping("/v1/users/{userId}")
  public ResponseEntity<ApiResponse> deleteUser(@PathVariable long userId) {
    if (userId < 0) {
      return createErrorResponse(
        HttpStatus.BAD_REQUEST, 
        "User ID should be a positive number");
    }

    userService.deleteUser(userId);
    
    return ResponseEntity
      .status(HttpStatus.NO_CONTENT)
      .body(new ApiResponse(
        true, 
        "User deleted successfully", 
        null));
  }
}
