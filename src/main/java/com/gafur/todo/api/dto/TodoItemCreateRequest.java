package com.gafur.todo.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;

public record TodoItemCreateRequest(
    @Size(max = 1000) @NotEmpty(message = "Description is required") String description,
    @NotNull(message = "Due date time is required")
        @FutureOrPresent(message = "Due date must not be in the past")
        ZonedDateTime dueDatetime) {}
