package com.eaglebank.api.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.api.model.user.User;
import com.eaglebank.api.service.user.UserService;
import com.eaglebank.api.service.jwt.JwtService;
import com.eaglebank.api.dto.login.LoginRequest;

@RestController
public class LoginController {
  @Autowired private UserService userService;
  @Autowired private JwtService JwtService; 
  @Autowired private BCryptPasswordEncoder encoder;

  @PostMapping("/v1/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    System.out.println("Entered the login function");

    String email = loginRequest.getEmail();
    String password = loginRequest.getPassword();

    User user = 
        userService.getUserByEmail(email);


    if (user == null || !encoder.matches(password, user.getPasswordHash())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    String token = JwtService.generateToken(email);
    return ResponseEntity.ok(Map.of("token", token));
  } 
}
