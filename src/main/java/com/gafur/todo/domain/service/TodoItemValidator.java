package com.gafur.todo.domain.service;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.exception.UpdateOperationNotAllowedException;

/** Validator for to-do item operations. */
public interface TodoItemValidator {

  /**
   * Validates if the update operation is allowed on the given to-do item entity.
   *
   * @param entity to-do item entity to validate
   * @throws UpdateOperationNotAllowedException if the update operation is not allowed
   */
  void validateUpdateOperationAllowed(TodoItemEntity entity);
}
