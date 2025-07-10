package com.eaglebank.api.model.user;

import java.time.LocalDateTime;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "users")
public class User {

  @Id
  @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
  private String id;

  @NotBlank
  private String name;

  @Embedded
  private Address address;

  @NotNull
  @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
  private String phoneNumber;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String passwordHash;

  private LocalDateTime createdTimestamp;

  private LocalDateTime updatedTimestamp;

  public User() {
  }

  public User(String id, String name, Address address, String phoneNumber, String email) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

  @PrePersist
  protected void onCreate() {
    createdTimestamp = LocalDateTime.now();
    updatedTimestamp = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedTimestamp = LocalDateTime.now();
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

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
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
