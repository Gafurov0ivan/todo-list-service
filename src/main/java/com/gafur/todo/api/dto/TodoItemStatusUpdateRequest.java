package com.gafur.todo.api.dto;

import jakarta.validation.constraints.NotNull;

public record TodoItemStatusUpdateRequest(
    @NotNull(message = "Status is required") TodoStatusApi status) {}
