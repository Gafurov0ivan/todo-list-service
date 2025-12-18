package com.gafur.todo.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gafur.todo.Application;
import com.gafur.todo.api.dto.*;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
public class TodoItemIntegrationTest {

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  private IntegrationTestUtil util;

  private static final String BASE_URL = "/v1/todo-items";
  private static final String DESCRIPTION = "Finish the home task";
  private static final String UPDATED_DESCRIPTION = "Updated description";

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    util = new IntegrationTestUtil(objectMapper, mockMvc, BASE_URL);
  }

  @Test
  public void createTodoItem() throws Exception {
    TodoItemDto createdItem = util.performCreateTodoItem(DESCRIPTION);

    assertTodoItem(createdItem, DESCRIPTION, TodoStatusApi.NOT_DONE);
  }

  @Test
  public void failCreateWhenDescriptionIsEmpty() throws Exception {
    TodoItemCreateRequest invalidRequest =
        new TodoItemCreateRequest("", ZonedDateTime.now().plusDays(1));

    String errorMessage =
        mockMvc
            .perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(errorMessage.contains("Description is required"));
  }

  @Test
  public void failCreateWhenDueInThePast() throws Exception {
    TodoItemCreateRequest invalidRequest =
        new TodoItemCreateRequest(DESCRIPTION, ZonedDateTime.now().minusDays(1));

    String errorMessage =
        mockMvc
            .perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(errorMessage.contains("Due date must not be in the past"));
  }

  @Test
  public void getAllTodoItems() throws Exception {
    util.performCreateTodoItem(DESCRIPTION);

    var items = util.performGetTodoItems(Optional.empty());

    assertEquals(1, items.size());
    assertTodoItem(items.getFirst(), DESCRIPTION, TodoStatusApi.NOT_DONE);
  }

  @Test
  public void getTodoItemsByStatus() throws Exception {
    UUID notDoneItemId = util.performCreateTodoItem(DESCRIPTION).id();
    UUID doneItemId = util.performCreateTodoItem(DESCRIPTION).id();
    util.performStatusUpdate(doneItemId, TodoStatusApi.DONE);

    var items = util.performGetTodoItems(Optional.of(TodoStatusApi.NOT_DONE));

    assertEquals(1, items.size());
    assertEquals(notDoneItemId, items.getFirst().id());
    assertEquals(TodoStatusApi.NOT_DONE, items.getFirst().status());
  }

  @Test
  public void getTodoItemById() throws Exception {
    UUID itemId = util.performCreateTodoItem(DESCRIPTION).id();

    String result =
        mockMvc
            .perform(get(BASE_URL + "/" + itemId))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    TodoItemDto item = objectMapper.readValue(result, TodoItemDto.class);

    assertTodoItem(item, DESCRIPTION, TodoStatusApi.NOT_DONE);
  }

  @Test
  public void failWhenGetNonExistentItem() throws Exception {
    util.performCreateTodoItem(DESCRIPTION);
    var nonExistentId = UUID.randomUUID().toString();

    mockMvc.perform(get(BASE_URL + "/" + nonExistentId)).andExpect(status().isNotFound());
  }

  @Test
  public void updateDescription() throws Exception {
    UUID itemId = util.performCreateTodoItem(DESCRIPTION).id();

    TodoItemDto updatedItem = util.performDescriptionUpdate(itemId, UPDATED_DESCRIPTION);

    assertTodoItem(updatedItem, UPDATED_DESCRIPTION, TodoStatusApi.NOT_DONE);
  }

  @Test
  public void failWhenUpdateNonExistentItem() throws Exception {
    var nonExistentId = UUID.randomUUID().toString();
    var updateRequest = new TodoItemUpdateRequest(UPDATED_DESCRIPTION);

    util.performFailedPatchRequest("/" + nonExistentId, updateRequest, HttpStatus.NOT_FOUND);
  }

  @Test
  public void updateStatusToDone() throws Exception {
    TodoItemDto createdItem = util.performCreateTodoItem(DESCRIPTION);

    TodoItemDto updatedItem = util.performStatusUpdate(createdItem.id(), TodoStatusApi.DONE);

    assertTodoItem(updatedItem, DESCRIPTION, TodoStatusApi.DONE);
    assertTrue(updatedItem.doneDatetime().isAfter(createdItem.creationDatetime()));
  }

  @Test
  public void updateStatusToNotDone() throws Exception {
    TodoItemDto createdItem = util.performCreateTodoItem(DESCRIPTION);
    util.performStatusUpdate(createdItem.id(), TodoStatusApi.DONE);

    TodoItemDto updatedItem = util.performStatusUpdate(createdItem.id(), TodoStatusApi.NOT_DONE);

    assertTodoItem(updatedItem, DESCRIPTION, TodoStatusApi.NOT_DONE);
    assertTrue(updatedItem.doneDatetime().isAfter(createdItem.creationDatetime()));
  }

  @Test
  public void failWhenUpdatePastDueItem() throws Exception {
    UUID itemId = util.performCreateTodoItem(DESCRIPTION).id();
    util.performStatusUpdate(itemId, TodoStatusApi.PAST_DUE);

    var updateRequest = new TodoItemUpdateRequest(UPDATED_DESCRIPTION);
    String errorMessage =
        util.performFailedPatchRequest("/" + itemId, updateRequest, HttpStatus.CONFLICT);

    assertTrue(
        errorMessage.contains(
            "Cannot update todo item which is in the past due status: " + itemId));
  }

  @Test
  public void failWhenUpdateStatusForNonExistentItem() throws Exception {
    var nonExistentId = UUID.randomUUID();
    var statusUpdateRequest = new TodoItemStatusUpdateRequest(TodoStatusApi.DONE);

    util.performFailedPatchRequest(
        "/" + nonExistentId + "/status", statusUpdateRequest, HttpStatus.NOT_FOUND);
  }

  /** Helper method to assert to-do item fields. */
  public void assertTodoItem(
      TodoItemDto createdItem, String expectedDescription, TodoStatusApi expectedStatus) {

    assertNotNull(createdItem.id());
    assertEquals(expectedDescription, createdItem.description());
    assertEquals(expectedStatus, createdItem.status());
    assertTrue(createdItem.creationDatetime().isBefore(createdItem.dueDatetime()));
  }
}
