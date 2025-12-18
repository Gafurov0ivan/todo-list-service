package com.gafur.todo.api.mapper;

import com.gafur.todo.api.dto.TodoItemCreateRequest;
import com.gafur.todo.api.dto.TodoItemDto;
import com.gafur.todo.api.dto.TodoStatusApi;
import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.model.CreateTodoItemCommand;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodoItemMapper {

  @Mapping(target = "id", source = "externalId")
  @Mapping(target = "status", expression = "java(toApiStatus(entity.getStatus()))")
  TodoItemDto toDto(TodoItemEntity entity);

  CreateTodoItemCommand toCommand(TodoItemCreateRequest entity);

  default TodoStatusApi toApiStatus(@NotNull TodoStatus domainStatus) {
    return TodoStatusApi.valueOf(domainStatus.name());
  }

  default TodoStatus toDomainStatus(@NotNull TodoStatusApi apiStatus) {
    return TodoStatus.valueOf(apiStatus.name());
  }
}
