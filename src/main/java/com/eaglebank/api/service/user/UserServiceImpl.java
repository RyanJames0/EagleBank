package com.eaglebank.api.service.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.dto.user.UserRequest;
import com.eaglebank.api.model.user.User;
import com.eaglebank.api.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;
  @Autowired private BCryptPasswordEncoder encoder;

  @Override
  public UserResponse createUser(UserRequest user) {
      User newUser = new User();
      newUser.setName(user.getName());
      newUser.setEmail(user.getEmail());

      String hashedPassword = encoder.encode(
          user.getPassword());
      newUser.setPasswordHash(hashedPassword);

      User savedUser = userRepository.save(newUser);

      return new UserResponse(savedUser);
  }


  @Override
  public UserResponse getUserById(Long userId) {
      return userRepository.findById(userId)
              .map(UserResponse::new)
              .orElse(null);
  }

  @Override
  public User getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      return null;
    }
    return user.get();
  }

  @Override
  public UserResponse updateUser(Long userId, UserRequest user) {
      return userRepository.findById(userId)
              .map(existingUser -> {
                  existingUser.setName(user.getName());
                  existingUser.setEmail(user.getEmail());
                  existingUser.setPasswordHash(user.getPassword());
                  userRepository.save(existingUser);
                  return new UserResponse(existingUser);
              })
              .orElse(null);
  }

  @Override
  public void deleteUser(Long userId) {
      userRepository.deleteById(userId);
  }

}
