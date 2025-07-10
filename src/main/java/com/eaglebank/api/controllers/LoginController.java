package com.eaglebank.api.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.dto.login.LoginRequest;
import com.eaglebank.api.model.ApiResponse;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.service.jwt.JwtService;
import com.eaglebank.api.service.user.UserService;
import com.eaglebank.api.utils.InputValidation;

import jakarta.validation.Valid;

@RestController
public class LoginController {
  @Autowired private UserService userService;
  @Autowired private JwtService jwtService;
  @Autowired private BCryptPasswordEncoder encoder;

  @PostMapping("/v1/login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody final LoginRequest loginRequest) {
    try {
      // Validate input
      if (InputValidation.isInvalidInput(loginRequest.getEmail())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Email is required", null));
      }

      if (InputValidation.isInvalidInput(loginRequest.getPassword())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Password is required", null));
      }

      String email = loginRequest.getEmail();
      String password = loginRequest.getPassword();

      User user = userService.getUserByEmail(email);

      if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "Invalid credentials", null));
      }

      // Validate password using BCrypt
      if (user.getPasswordHash() == null || !encoder.matches(password, user.getPasswordHash())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse(false, "Invalid credentials", null));
      }

      String token = jwtService.generateToken(email);
      Map<String, String> tokenData = Map.of("token", token);
      
      return ResponseEntity.ok()
          .body(new ApiResponse(true, "Login successful", tokenData));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(false, "An unexpected error occurred", null));
    }
  }
}
