package com.eaglebank.api.dto.user;

import java.time.LocalDateTime;

import com.eaglebank.api.model.user.Address;
import com.eaglebank.api.model.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserResponse {

  private String id;
  private String name;
  private Address address;
  private String phoneNumber;
  private String email;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdTimestamp;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime updatedTimestamp;

  public UserResponse() {
  }

  public UserResponse(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.address = user.getAddress();
    this.phoneNumber = user.getPhoneNumber();
    this.email = user.getEmail();
    this.createdTimestamp = user.getCreatedTimestamp();
    this.updatedTimestamp = user.getUpdatedTimestamp();
  }

  public UserResponse(String id, String name, Address address, String phoneNumber, String email,
                     LocalDateTime createdTimestamp, LocalDateTime updatedTimestamp) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.createdTimestamp = createdTimestamp;
    this.updatedTimestamp = updatedTimestamp;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public LocalDateTime getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
  }
}
