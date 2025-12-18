package com.gafur.todo.api.exception;

public class IllegalStatusException extends IllegalArgumentException {
  public IllegalStatusException(String message) {
    super(message);
  }
}
