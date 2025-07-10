package com.eaglebank.api.dto.error;

import java.util.List;

public class BadRequestErrorResponse {

  private String message;
  private List<ValidationError> details;

  public BadRequestErrorResponse() {
  }

  public BadRequestErrorResponse(String message, List<ValidationError> details) {
    this.message = message;
    this.details = details;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ValidationError> getDetails() {
    return details;
  }

  public void setDetails(List<ValidationError> details) {
    this.details = details;
  }

  public static class ValidationError {
    private String field;
    private String message;
    private String type;

    public ValidationError() {
    }

    public ValidationError(String field, String message, String type) {
      this.field = field;
      this.message = message;
      this.type = type;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}