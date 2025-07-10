package com.eaglebank.api.service.user;

import com.eaglebank.api.dto.user.CreateUserRequest;
import com.eaglebank.api.dto.user.UpdateUserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.model.user.User;

public interface UserService {

  UserResponse createUser(CreateUserRequest request);
  
  UserResponse getUserById(String userId);
  
  User getUserByEmail(String email);

  UserResponse updateUser(String userId, UpdateUserRequest request);
  
  void deleteUser(String userId);
}
