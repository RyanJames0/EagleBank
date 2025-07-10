package com.eaglebank.api.service.user;

import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.dto.user.UserRequest;

public interface UserService {

  public UserResponse createUser(UserRequest user);
  public UserResponse getUserById(Long userId);
  public User getUserByEmail(String email);

  public UserResponse updateUser(Long userId, UserRequest user);
  public void deleteUser(Long userId);

}
