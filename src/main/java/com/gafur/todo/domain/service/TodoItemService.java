package com.gafur.todo.domain.service;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.model.CreateTodoItemCommand;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** To-do item service. Provides methods for managing to-do items. */
public interface TodoItemService {

  /**
   * Find to-do items by status
   *
   * @return to-do items list
   */
  List<TodoItemEntity> findTodoItemsByStatus(TodoStatus status);

  /**
   * Find all to-do items
   *
   * @return to-do items list
   */
  List<TodoItemEntity> findTodoItems();

  /**
   * Find to-do item by external id
   *
   * @param externalId the external id
   * @return to-do item
   */
  Optional<TodoItemEntity> findTodoItemById(UUID externalId);

  /**
   * Create to-do item
   *
   * @param createRequest the create request
   * @return created to-do item
   */
  TodoItemEntity createTodoItem(CreateTodoItemCommand createRequest);

  /**
   * Update to-do item description
   *
   * @param externalId the external id
   * @param description to-to item description
   * @return updated to-do item
   */
  TodoItemEntity updateTodoItemDescription(UUID externalId, String description);

  /**
   * Update to-do item status
   *
   * @param externalId the external id
   * @param status to-to item status
   * @return updated to-do item
   */
  TodoItemEntity updateTodoItemStatus(UUID externalId, TodoStatus status);
}
