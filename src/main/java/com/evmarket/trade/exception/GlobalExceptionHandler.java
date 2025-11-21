package com.evmarket.trade.exception;

import com.evmarket.trade.response.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        BaseResponse<Map<String, String>> response = BaseResponse.<Map<String, String>>builder()
                .message(ErrorHandler.VALIDATION_FAILED.getMessage())
                .success(false)
                .data(errors)
                .build();
        
        return ResponseEntity.status(ErrorHandler.VALIDATION_FAILED.getStatusCode()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleJsonParse(HttpMessageNotReadableException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(ErrorHandler.INVALID_INPUT.getMessage())
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message("Invalid parameter type")
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(ErrorHandler.VALIDATION_FAILED.getMessage())
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        // Extract more specific error message from the exception
        String errorMessage = "Data integrity violation";
        String exceptionMessage = ex.getMessage();
        if (exceptionMessage != null) {
            // Check for common constraint violations
            if (exceptionMessage.contains("email") || exceptionMessage.contains("EMAIL")) {
                errorMessage = "Email already exists";
            } else if (exceptionMessage.contains("username") || exceptionMessage.contains("USERNAME")) {
                errorMessage = "Username already exists";
            } else if (exceptionMessage.contains("identity_card") || exceptionMessage.contains("IDENTITY_CARD")) {
                errorMessage = "Identity card already exists";
            } else if (exceptionMessage.contains("Duplicate entry")) {
                errorMessage = "Duplicate entry: " + exceptionMessage;
            }
        }
        
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(errorMessage)
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppException(AppException ex) {
        ErrorHandler err = ex.getErrorHandler();
        
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(err.getMessage())
                .success(false)
                .data(null)
                .build();
        
        return ResponseEntity.status(err.getStatusCode()).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse<Void>> handleConflict(IllegalStateException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(ex.getMessage())
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(ex.getMessage())
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneric(Exception ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .message(ErrorHandler.UNCATEGORIZED_EXCEPTION.getMessage())
                .success(false)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


