package com.eaglebank.api.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eaglebank.api.dto.error.BadRequestErrorResponse;
import com.eaglebank.api.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BadRequestErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    List<BadRequestErrorResponse.ValidationError> validationErrors = new ArrayList<>();
    
    // Check if it's an enum validation error
    if (ex.getCause() instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatEx = (InvalidFormatException) ex.getCause();
      
      // Check if the target type is an enum
      if (invalidFormatEx.getTargetType() != null && invalidFormatEx.getTargetType().isEnum()) {
        String fieldName = getFieldNameFromPath(invalidFormatEx.getPath());
        String invalidValue = invalidFormatEx.getValue().toString();
        String enumValues = getEnumValues(invalidFormatEx.getTargetType());
        
        String message = String.format("Invalid value '%s'. Allowed values are: %s", invalidValue, enumValues);
        validationErrors.add(new BadRequestErrorResponse.ValidationError(fieldName, message, "enum"));
        
        BadRequestErrorResponse response = new BadRequestErrorResponse("Validation failed", validationErrors);
        return ResponseEntity.badRequest().body(response);
      }
    }
    
    // For other JSON parsing errors, return a generic validation error
    validationErrors.add(new BadRequestErrorResponse.ValidationError("request", "Invalid request format", "format"));
    BadRequestErrorResponse response = new BadRequestErrorResponse("Validation failed", validationErrors);
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

  private String getFieldNameFromPath(List<com.fasterxml.jackson.databind.JsonMappingException.Reference> path) {
    if (path == null || path.isEmpty()) {
      return "unknown";
    }
    
    // Get the last field name from the path
    com.fasterxml.jackson.databind.JsonMappingException.Reference lastRef = path.get(path.size() - 1);
    return lastRef.getFieldName() != null ? lastRef.getFieldName() : "unknown";
  }

  private String getEnumValues(Class<?> enumClass) {
    if (!enumClass.isEnum()) {
      return "";
    }
    
    Object[] enumConstants = enumClass.getEnumConstants();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < enumConstants.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(enumConstants[i].toString());
    }
    return sb.toString();
  }
}