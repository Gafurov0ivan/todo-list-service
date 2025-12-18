package com.gafur.todo.api.controller;

import com.gafur.todo.api.dto.TodoItemCreateRequest;
import com.gafur.todo.api.dto.TodoItemDto;
import com.gafur.todo.api.dto.TodoItemStatusUpdateRequest;
import com.gafur.todo.api.dto.TodoItemUpdateRequest;
import com.gafur.todo.api.dto.TodoStatusApi;
import com.gafur.todo.api.mapper.TodoItemMapper;
import com.gafur.todo.domain.service.TodoItemService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TodoItemController implements TodoItemApiContract {

  private TodoItemService todoItemService;
  private TodoItemMapper mapper;

  @Override
  public ResponseEntity<TodoItemDto> createTodoItem(TodoItemCreateRequest createRequest) {
    var createCommand = mapper.toCommand(createRequest);
    var createdItem = mapper.toDto(todoItemService.createTodoItem(createCommand));
    return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
  }

  @Override
  public ResponseEntity<List<TodoItemDto>> findTodoItems(String status) {
    if (status != null) {
      var domainStatus = mapper.toDomainStatus(TodoStatusApi.fromValue(status));
      var itemsByStatus = todoItemService.findTodoItemsByStatus(domainStatus);
      return ResponseEntity.ok(itemsByStatus.stream().map(mapper::toDto).toList());
    }
    var items = todoItemService.findTodoItems();
    return ResponseEntity.ok(items.stream().map(mapper::toDto).toList());
  }

  @Override
  public ResponseEntity<TodoItemDto> findTodoItemById(UUID id) {
    var itemById = todoItemService.findTodoItemById(id);
    return itemById
        .map(optional -> ResponseEntity.ok(mapper.toDto(optional)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<TodoItemDto> updateTodoItemDescription(
      UUID id, TodoItemUpdateRequest updateRequest) {
    var updatedItem = todoItemService.updateTodoItemDescription(id, updateRequest.description());
    return ResponseEntity.ok(mapper.toDto(updatedItem));
  }

  @Override
  public ResponseEntity<TodoItemDto> updateTodoItemStatus(
      UUID id, TodoItemStatusUpdateRequest statusUpdateRequest) {
    var domainStatus = mapper.toDomainStatus(statusUpdateRequest.status());
    var updatedItem = todoItemService.updateTodoItemStatus(id, domainStatus);
    return ResponseEntity.ok(mapper.toDto(updatedItem));
  }
}
