package com.gafur.todo.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.exception.UpdateOperationNotAllowedException;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TodoItemPastDueValidatorTest {

  private TodoItemPastDueValidator validator;

  private static final String DESCRIPTION = "Test description";

  @BeforeEach
  void setUp() {
    validator = new TodoItemPastDueValidator();
  }

  @Test
  void passedWhenStatusIsNotDone() {
    var entity = new TodoItemEntity(DESCRIPTION, ZonedDateTime.now().plusDays(1));
    entity.setStatus(TodoStatus.NOT_DONE);

    assertDoesNotThrow(() -> validator.validateUpdateOperationAllowed(entity));
  }

  @Test
  void passedWhenStatusIsDone() {
    var entity = new TodoItemEntity(DESCRIPTION, ZonedDateTime.now().plusDays(1));
    entity.setStatus(TodoStatus.DONE);

    assertDoesNotThrow(() -> validator.validateUpdateOperationAllowed(entity));
  }

  @Test
  void failWhenStatusIsPastDue() {
    var entity = new TodoItemEntity(DESCRIPTION, ZonedDateTime.now().plusDays(1));
    entity.setStatus(TodoStatus.PAST_DUE);

    UpdateOperationNotAllowedException exception =
        assertThrows(
            UpdateOperationNotAllowedException.class,
            () -> validator.validateUpdateOperationAllowed(entity));

    assertEquals(
        "Cannot update todo item which is in the past due status: " + entity.getExternalId(),
        exception.getMessage());
  }
}
