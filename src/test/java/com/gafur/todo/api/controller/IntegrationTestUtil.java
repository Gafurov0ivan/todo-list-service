package com.gafur.todo.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gafur.todo.api.dto.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AllArgsConstructor
public class IntegrationTestUtil {

  private ObjectMapper objectMapper;
  private MockMvc mockMvc;
  private String BASE_URL;

  /** Helper method to create a to-do item and return the DTO. */
  public TodoItemDto performCreateTodoItem(String description) throws Exception {
    TodoItemCreateRequest createRequest =
        new TodoItemCreateRequest(description, ZonedDateTime.now().plusDays(1));
    MvcResult result =
        mockMvc
            .perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, TodoItemDto.class);
  }

  /** Helper method to get to-do items and return the DTO list. */
  public List<TodoItemDto> performGetTodoItems(Optional<TodoStatusApi> status) throws Exception {
    MvcResult result;
    if (status.isPresent()) {
      result =
          mockMvc
              .perform(get(BASE_URL).param("status", status.get().getValue()))
              .andExpect(status().isOk())
              .andReturn();
    } else {
      result = mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andReturn();
    }

    String responseBody = result.getResponse().getContentAsString();
    return objectMapper.readValue(
        responseBody,
        objectMapper.getTypeFactory().constructCollectionType(List.class, TodoItemDto.class));
  }

  /** Helper method to update to-do item description and return the DTO. */
  public TodoItemDto performDescriptionUpdate(UUID id, String newDescription) throws Exception {
    var updateRequest = new TodoItemUpdateRequest(newDescription);
    MvcResult result =
        mockMvc
            .perform(
                patch(BASE_URL + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andReturn();
    String responseBody = result.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, TodoItemDto.class);
  }

  /** Helper method to update to-do item status and return the DTO. */
  public TodoItemDto performStatusUpdate(UUID id, TodoStatusApi newStatus) throws Exception {
    var updateRequest = new TodoItemStatusUpdateRequest(newStatus);
    MvcResult result =
        mockMvc
            .perform(
                patch(BASE_URL + "/" + id + "/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andReturn();
    String responseBody = result.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, TodoItemDto.class);
  }

  /**
   * Helper method to perform patch request that is expected to fail and return the error message.
   */
  public <T> String performFailedPatchRequest(String url, T request, HttpStatus expectedHttpStatus)
      throws Exception {
    return mockMvc
        .perform(
            patch(BASE_URL + url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(expectedHttpStatus.value()))
        .andReturn()
        .getResponse()
        .getContentAsString();
  }
}
