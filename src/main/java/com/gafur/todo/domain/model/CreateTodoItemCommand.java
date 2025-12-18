package com.gafur.todo.domain.model;

import java.time.ZonedDateTime;

public record CreateTodoItemCommand(String description, ZonedDateTime dueDatetime) {}
