package com.gafur.todo.domain.service;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.exception.UpdateOperationNotAllowedException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TodoItemPastDueValidator implements TodoItemValidator {

  @Override
  public void validateUpdateOperationAllowed(@NotNull TodoItemEntity entity) {
    if (TodoStatus.PAST_DUE == entity.getStatus()) {
      throw new UpdateOperationNotAllowedException(
          "Cannot update todo item which is in the past due status: " + entity.getExternalId());
    }
  }
}
