package com.eaglebank.api.dto.user;

import com.eaglebank.api.model.user.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateUserRequest {

  @NotBlank
  private String name;

  @Valid
  @NotNull
  private Address address;

  @NotNull
  @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
  private String phoneNumber;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String password;

  public CreateUserRequest() {
  }

  public CreateUserRequest(String name, Address address, String phoneNumber, String email) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.email = email;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}