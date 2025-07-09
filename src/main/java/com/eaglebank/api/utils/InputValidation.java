package com.eaglebank.api.utils;


public class InputValidation {
  public static boolean isInvalidInput(String input) {
    return input == null || input.trim().isEmpty();
  }
}
