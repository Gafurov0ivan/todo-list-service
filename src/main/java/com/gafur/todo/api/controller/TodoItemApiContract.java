package com.gafur.todo.api.controller;

import com.gafur.todo.api.dto.TodoItemCreateRequest;
import com.gafur.todo.api.dto.TodoItemDto;
import com.gafur.todo.api.dto.TodoItemStatusUpdateRequest;
import com.gafur.todo.api.dto.TodoItemUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("v1/todo-items")
public interface TodoItemApiContract {

  @Operation(summary = "Create a new todo item")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TodoItemDto.class))
            }),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
      })
  @PostMapping
  ResponseEntity<TodoItemDto> createTodoItem(@Valid @RequestBody TodoItemCreateRequest request);

  @Operation(summary = "Find list of todo items")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = TodoItemDto.class)))
            })
      })
  @GetMapping
  ResponseEntity<List<TodoItemDto>> findTodoItems(
      @RequestParam(value = "status", required = false) String status);

  @Operation(summary = "Find todo item by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TodoItemDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Todo item not found", content = @Content)
      })
  @GetMapping("/{id}")
  ResponseEntity<TodoItemDto> findTodoItemById(@PathVariable("id") UUID id);

  @Operation(summary = "Update todo item description")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TodoItemDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Todo item not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(
            responseCode = "409",
            description = "Operation is now allowed",
            content = @Content)
      })
  @PatchMapping("/{id}")
  ResponseEntity<TodoItemDto> updateTodoItemDescription(
      @PathVariable("id") UUID id, @Valid @RequestBody TodoItemUpdateRequest request);

  @Operation(summary = "Update todo item status")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TodoItemDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Todo item not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
        @ApiResponse(
            responseCode = "409",
            description = "Operation is now allowed",
            content = @Content)
      })
  @PatchMapping("/{id}/status")
  ResponseEntity<TodoItemDto> updateTodoItemStatus(
      @PathVariable("id") UUID id, @Valid @RequestBody TodoItemStatusUpdateRequest request);
}
