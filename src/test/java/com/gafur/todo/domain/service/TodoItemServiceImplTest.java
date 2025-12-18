package com.gafur.todo.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.exception.UpdateOperationNotAllowedException;
import com.gafur.todo.domain.model.CreateTodoItemCommand;
import com.gafur.todo.domain.repository.TodoItemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

  @Mock private TodoItemRepository todoItemRepository;

  @Mock private TodoItemValidator validator;

  @InjectMocks private TodoItemServiceImpl todoItemService;

  private TodoItemEntity entity;
  private UUID externalId;

  private static final String NEW_DESCRIPTION = "New description";

  @BeforeEach
  void setUp() {
    externalId = UUID.randomUUID();
    entity = new TodoItemEntity("Test description", ZonedDateTime.now().plusDays(1));
  }

  @Test
  void findItemsByStatus() {
    TodoStatus status = TodoStatus.NOT_DONE;
    List<TodoItemEntity> expectedItems = List.of(entity);
    when(todoItemRepository.findByStatusOrderByDueDatetimeAsc(status)).thenReturn(expectedItems);

    List<TodoItemEntity> result = todoItemService.findTodoItemsByStatus(status);

    assertEquals(expectedItems, result);
    verify(todoItemRepository).findByStatusOrderByDueDatetimeAsc(status);
  }

  @Test
  void findAllItems() {
    List<TodoItemEntity> expectedItems = List.of(entity);
    when(todoItemRepository.findAllByOrderByDueDatetimeAsc()).thenReturn(expectedItems);

    List<TodoItemEntity> result = todoItemService.findTodoItems();

    assertEquals(expectedItems, result);
    verify(todoItemRepository).findAllByOrderByDueDatetimeAsc();
  }

  @Test
  void findItemById() {
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));

    Optional<TodoItemEntity> result = todoItemService.findTodoItemById(externalId);

    assertEquals(entity, result.get());
    verify(todoItemRepository).findByExternalId(externalId);
  }

  @Test
  void returnEmptyWhenItemNotFound() {
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    Optional<TodoItemEntity> result = todoItemService.findTodoItemById(externalId);

    assertFalse(result.isPresent());
    verify(todoItemRepository).findByExternalId(externalId);
  }

  @Test
  void createItem() {
    CreateTodoItemCommand command =
        new CreateTodoItemCommand("New task", ZonedDateTime.now().plusDays(2));
    TodoItemEntity savedEntity = new TodoItemEntity(command.description(), command.dueDatetime());
    when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(savedEntity);

    TodoItemEntity result = todoItemService.createTodoItem(command);

    assertEquals(savedEntity, result);
    verify(todoItemRepository).save(any(TodoItemEntity.class));
  }

  @Test
  void updateItemDescription() {
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));

    TodoItemEntity result = todoItemService.updateTodoItemDescription(externalId, NEW_DESCRIPTION);

    assertEquals(NEW_DESCRIPTION, result.getDescription());
    verify(todoItemRepository).findByExternalId(externalId);
  }

  @Test
  void failWhenUpdateNonExistentItem() {
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> todoItemService.updateTodoItemDescription(externalId, NEW_DESCRIPTION));
    verify(todoItemRepository).findByExternalId(externalId);
    verify(validator, never()).validateUpdateOperationAllowed(any());
  }

  @Test
  void failWhenUpdateDescriptionNotAllowed() {
    entity.setStatus(TodoStatus.PAST_DUE);
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
    doThrow(new UpdateOperationNotAllowedException("Cannot update past due item"))
        .when(validator)
        .validateUpdateOperationAllowed(entity);

    assertThrows(
        UpdateOperationNotAllowedException.class,
        () -> todoItemService.updateTodoItemDescription(externalId, NEW_DESCRIPTION));
    verify(validator).validateUpdateOperationAllowed(entity);
  }

  @Test
  void updateItemStatus() {
    TodoStatus newStatus = TodoStatus.DONE;
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));

    TodoItemEntity result = todoItemService.updateTodoItemStatus(externalId, newStatus);

    assertEquals(newStatus, result.getStatus());
    verify(todoItemRepository).findByExternalId(externalId);
  }

  @Test
  void failWhenUpdateNonExistentItemStatus() {
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> todoItemService.updateTodoItemStatus(externalId, TodoStatus.DONE));
    verify(todoItemRepository).findByExternalId(externalId);
  }

  @Test
  void failWhenUpdateStatusNotAllowed() {
    entity.setStatus(TodoStatus.PAST_DUE);
    when(todoItemRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
    doThrow(new UpdateOperationNotAllowedException("Cannot update past due item"))
        .when(validator)
        .validateUpdateOperationAllowed(entity);

    assertThrows(
        UpdateOperationNotAllowedException.class,
        () -> todoItemService.updateTodoItemStatus(externalId, TodoStatus.DONE));
    verify(validator).validateUpdateOperationAllowed(entity);
  }
}
