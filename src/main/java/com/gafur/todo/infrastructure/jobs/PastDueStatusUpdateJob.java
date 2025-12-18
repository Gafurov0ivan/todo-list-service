package com.gafur.todo.infrastructure.jobs;

import com.gafur.todo.domain.entity.TodoStatus;
import com.gafur.todo.domain.repository.TodoItemRepository;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Job that runs periodically to update the status of to-do items that are past their due date. */
@Slf4j
@Component
@AllArgsConstructor
public class PastDueStatusUpdateJob {

  private final TodoItemRepository todoItemRepository;

  private static final long SCHEDULER_RUN_DELAY_MS = 60000; // 1 minute

  @Transactional
  @Scheduled(fixedDelay = SCHEDULER_RUN_DELAY_MS)
  public void updatePastDueItems() {
    try {
      log.debug("Running past due status update job");

      var pastDueItems =
          todoItemRepository.findByStatusAndDueDatetimeBefore(
              TodoStatus.NOT_DONE, ZonedDateTime.now());

      if (!pastDueItems.isEmpty()) {
        pastDueItems.forEach(item -> item.setStatus(TodoStatus.PAST_DUE));
        todoItemRepository.saveAll(pastDueItems);
      }
      log.info(
          "Successfully updated {} items to {} status", pastDueItems.size(), TodoStatus.PAST_DUE);
    } catch (Exception exception) {
      log.error("Failed to update past due items", exception);
    }
  }
}
