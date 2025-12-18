package com.gafur.todo.domain.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "todo_items")
public class TodoItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, updatable = false)
  private UUID externalId;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TodoStatus status;

  @Column(nullable = false, updatable = false)
  private ZonedDateTime creationDatetime;

  @Column(nullable = false)
  private ZonedDateTime dueDatetime;

  private ZonedDateTime doneDatetime;

  public TodoItemEntity(String description, ZonedDateTime dueDatetime) {
    this.description = Objects.requireNonNull(description, "Description cannot be null");
    this.dueDatetime = Objects.requireNonNull(dueDatetime, "Due datetime cannot be null");
  }

  @PrePersist
  protected void onCreate() {
    externalId = UUID.randomUUID();
    creationDatetime = ZonedDateTime.now();
    status = TodoStatus.NOT_DONE;
  }

  public void setDescription(String description) {
    this.description = Objects.requireNonNull(description, "Description cannot be null");
  }

  public void setStatus(TodoStatus status) {
    this.status = Objects.requireNonNull(status, "Status cannot be null");
    if (status == TodoStatus.DONE) {
      this.doneDatetime = ZonedDateTime.now();
    }
  }
}
