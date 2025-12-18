package com.gafur.todo.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gafur.todo.domain.entity.TodoItemEntity;
import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.repository.TodoItemRepository;
import com.gafur.todo.infrastructure.jobs.PastDueStatusUpdateJob;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PastDueStatusUpdateJobTest {

  @Mock private TodoItemRepository todoItemRepository;

  @InjectMocks private PastDueStatusUpdateJob pastDueStatusUpdateJob;

  @Test
  void updateItemsToPastDue() {
    TodoItemEntity notDoneItem = new TodoItemEntity("Task", ZonedDateTime.now().minusHours(2));
    notDoneItem.setStatus(TodoStatus.NOT_DONE);
    List<TodoItemEntity> pastDueItems = List.of(notDoneItem, notDoneItem);

    when(todoItemRepository.findByStatusAndDueDatetimeBefore(
            eq(TodoStatus.NOT_DONE), any(ZonedDateTime.class)))
        .thenReturn(pastDueItems);

    pastDueStatusUpdateJob.updatePastDueItems();

    assertEquals(TodoStatus.PAST_DUE, notDoneItem.getStatus());
    verify(todoItemRepository).saveAll(pastDueItems);
  }
}
