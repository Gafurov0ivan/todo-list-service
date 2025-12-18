package com.gafur.todo.api;

import com.gafur.todo.api.dto.ErrorResponse;
import com.gafur.todo.api.exception.IllegalStatusException;
import com.gafur.todo.domain.exception.UpdateOperationNotAllowedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

  private static final String VALIDATION_ERROR = "VALIDATION_ERROR";

  @ExceptionHandler(
      value = {
        UpdateOperationNotAllowedException.class,
      })
  protected ResponseEntity<ErrorResponse> handleNotAllowedException(
      Exception exception, WebRequest request) {
    var errorType = "OPERATION_IS_NOW_ALLOWED";
    log.warn("{}: {}", errorType, exception.getMessage(), exception);
    var errorResponse = new ErrorResponse(errorType, exception.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class, IllegalStatusException.class})
  protected ResponseEntity<ErrorResponse> handleValidationFailed(
      Exception exception, WebRequest request) {
    log.warn("{}: {}", VALIDATION_ERROR, exception.getMessage(), exception);
    var errorResponse = new ErrorResponse(VALIDATION_ERROR, exception.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception, WebRequest request) {

    String errorMessage =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse(VALIDATION_ERROR);

    log.warn("{}: {}", VALIDATION_ERROR, errorMessage);
    var errorResponse = new ErrorResponse(VALIDATION_ERROR, errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(value = {EntityNotFoundException.class})
  protected ResponseEntity<?> handleNotFound(RuntimeException exception, WebRequest request) {
    log.info("Item not found: {}", exception.getMessage(), exception);
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<ErrorResponse> handleInternalServerError(
      Exception exception, WebRequest request) {
    var errorType = "INTERNAL_SERVER_ERROR";
    log.error("{}: {}", errorType, exception.getMessage(), exception);
    var errorResponse = new ErrorResponse(errorType, exception.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
