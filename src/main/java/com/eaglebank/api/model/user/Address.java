package com.eaglebank.api.model.user;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Address {

  @NotBlank
  private String line1;

  private String line2;

  private String line3;

  @NotBlank
  private String town;

  @NotBlank
  private String county;

  @NotBlank
  private String postcode;

  public Address() {
  }

  public Address(String line1, String line2, String line3, String town, String county, String postcode) {
    this.line1 = line1;
    this.line2 = line2;
    this.line3 = line3;
    this.town = town;
    this.county = county;
    this.postcode = postcode;
  }

  public String getLine1() {
    return line1;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }

  public String getLine2() {
    return line2;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }

  public String getLine3() {
    return line3;
  }

  public void setLine3(String line3) {
    this.line3 = line3;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }
}