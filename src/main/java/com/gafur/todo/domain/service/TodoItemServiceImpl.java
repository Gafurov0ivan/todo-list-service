package com.gafur.todo.domain.service;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.model.CreateTodoItemCommand;
import com.gafur.todo.domain.repository.TodoItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TodoItemServiceImpl implements TodoItemService {

  private TodoItemRepository todoItemRepository;
  private TodoItemValidator validator;

  @Override
  public List<TodoItemEntity> findTodoItemsByStatus(TodoStatus status) {
    return todoItemRepository.findByStatusOrderByDueDatetimeAsc(status);
  }

  @Override
  public List<TodoItemEntity> findTodoItems() {
    return todoItemRepository.findAllByOrderByDueDatetimeAsc();
  }

  @Override
  public Optional<TodoItemEntity> findTodoItemById(UUID externalId) {
    return todoItemRepository.findByExternalId(externalId);
  }

  @Override
  public TodoItemEntity createTodoItem(CreateTodoItemCommand createCommand) {
    TodoItemEntity newTodoItem =
        new TodoItemEntity(createCommand.description(), createCommand.dueDatetime());
    return todoItemRepository.save(newTodoItem);
  }

  @Override
  @Transactional
  public TodoItemEntity updateTodoItemDescription(UUID externalId, String description) {
    TodoItemEntity entity = findByExternalIdOrThrow(externalId);
    validator.validateUpdateOperationAllowed(entity);

    entity.setDescription(description);
    return entity;
  }

  @Override
  @Transactional
  public TodoItemEntity updateTodoItemStatus(UUID externalId, TodoStatus status) {
    TodoItemEntity entity = findByExternalIdOrThrow(externalId);
    validator.validateUpdateOperationAllowed(entity);

    entity.setStatus(status);
    return entity;
  }

  private TodoItemEntity findByExternalIdOrThrow(UUID externalId) {
    return todoItemRepository
        .findByExternalId(externalId)
        .orElseThrow(() -> new EntityNotFoundException("Todo item not found: " + externalId));
  }
}
