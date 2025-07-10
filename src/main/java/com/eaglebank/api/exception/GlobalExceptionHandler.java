package com.eaglebank.api.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eaglebank.api.dto.error.BadRequestErrorResponse;
import com.eaglebank.api.dto.error.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BadRequestErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
    List<BadRequestErrorResponse.ValidationError> validationErrors = new ArrayList<>();
    
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      String field = fieldError.getField();
      String message = fieldError.getDefaultMessage();
      String type = determineValidationType(fieldError);
      
      validationErrors.add(new BadRequestErrorResponse.ValidationError(field, message, type));
    }
    
    BadRequestErrorResponse response = new BadRequestErrorResponse(
        "Validation failed", 
        validationErrors
    );
    
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<BadRequestErrorResponse> handleConstraintViolationErrors(ConstraintViolationException ex) {
    List<BadRequestErrorResponse.ValidationError> validationErrors = new ArrayList<>();
    
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String field = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      String type = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
      
      validationErrors.add(new BadRequestErrorResponse.ValidationError(field, message, type));
    }
    
    BadRequestErrorResponse response = new BadRequestErrorResponse(
        "Validation failed", 
        validationErrors
    );
    
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("An unexpected error occurred"));
  }

  private String determineValidationType(FieldError fieldError) {
    String code = fieldError.getCode();
    if (code == null) {
      return "validation";
    }
    
    switch (code) {
      case "NotNull":
        return "required";
      case "NotBlank":
        return "required";
      case "Email":
        return "format";
      case "Pattern":
        return "format";
      case "DecimalMin":
        return "range";
      case "DecimalMax":
        return "range";
      default:
        return "validation";
    }
  }
}