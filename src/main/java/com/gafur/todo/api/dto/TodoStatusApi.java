package com.gafur.todo.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gafur.todo.api.exception.IllegalStatusException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public enum TodoStatusApi {
  NOT_DONE("not done"),
  DONE("done"),
  PAST_DUE("past due");

  private final String value;

  TodoStatusApi(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static TodoStatusApi fromValue(@NotNull String value) {
    for (TodoStatusApi status : TodoStatusApi.values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalStatusException("Invalid status: " + value);
  }
}
