package com.gafur.todo.api.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record TodoItemDto(
    UUID id,
    String description,
    TodoStatusApi status,
    ZonedDateTime creationDatetime,
    ZonedDateTime dueDatetime,
    ZonedDateTime doneDatetime) {}
