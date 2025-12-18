package com.gafur.todo.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TodoItemUpdateRequest(
    @NotEmpty(message = "Description is required") @Size(max = 1000) String description) {}
