package com.eaglebank.api.service.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaglebank.api.dto.user.CreateUserRequest;
import com.eaglebank.api.dto.user.UpdateUserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;
  @Autowired private BankAccountRepository bankAccountRepository;

  @Override
  public UserResponse createUser(CreateUserRequest request) {
    // Generate user ID in the format usr-[A-Za-z0-9]+
    String userId = "usr-" + UUID.randomUUID().toString().replace("-", "");
    
    User user = new User();
    user.setId(userId);
    user.setName(request.getName());
    user.setAddress(request.getAddress());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setEmail(request.getEmail());
    
    User savedUser = userRepository.save(user);
    return new UserResponse(savedUser);
  }

  @Override
  public UserResponse getUserById(String userId) {
    return userRepository.findById(userId)
        .map(UserResponse::new)
        .orElse(null);
  }

  @Override
  public User getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.orElse(null);
  }

  @Override
  public UserResponse updateUser(String userId, UpdateUserRequest request) {
    return userRepository.findById(userId)
        .map(existingUser -> {
          if (request.getName() != null) {
            existingUser.setName(request.getName());
          }
          if (request.getAddress() != null) {
            existingUser.setAddress(request.getAddress());
          }
          if (request.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(request.getPhoneNumber());
          }
          if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
          }
          
          userRepository.save(existingUser);
          return new UserResponse(existingUser);
        })
        .orElse(null);
  }

  @Override
  public void deleteUser(String userId) {
    // First check if user exists
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }
    
    User user = userOptional.get();
    
    // Check if user has any bank accounts
    boolean hasAccounts = bankAccountRepository.existsByUserEmail(user.getEmail());
    if (hasAccounts) {
      throw new IllegalStateException("Cannot delete user: User has existing bank accounts");
    }
    
    // If no bank accounts, proceed with deletion
    userRepository.deleteById(userId);
  }
}
