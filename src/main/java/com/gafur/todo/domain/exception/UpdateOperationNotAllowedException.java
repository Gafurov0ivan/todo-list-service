package com.gafur.todo.domain.exception;

public class UpdateOperationNotAllowedException extends RuntimeException {
  public UpdateOperationNotAllowedException(String message) {
    super(message);
  }
}
