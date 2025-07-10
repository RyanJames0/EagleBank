package com.eaglebank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eaglebank.api.model.user.User;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByEmail(String email);
  
  boolean existsByEmail(String email);
}
