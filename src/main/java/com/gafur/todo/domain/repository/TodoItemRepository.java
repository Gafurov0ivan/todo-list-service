package com.gafur.todo.domain.repository;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** To-do item repository. */
@Repository
public interface TodoItemRepository extends JpaRepository<TodoItemEntity, UUID> {

  Optional<TodoItemEntity> findByExternalId(UUID externalId);

  List<TodoItemEntity> findByStatusOrderByDueDatetimeAsc(TodoStatus status);

  List<TodoItemEntity> findAllByOrderByDueDatetimeAsc();

  List<TodoItemEntity> findByStatusAndDueDatetimeBefore(
      TodoStatus status, ZonedDateTime dueDateTime);
}
