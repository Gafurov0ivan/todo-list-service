package com.gafur.todo.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gafur.todo.Application;
import com.gafur.todo.api.dto.*;
import java.time.ZonedDateTime;
import java.util.List;
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
  public void getAllTodoItems() throws Exception {
    util.performCreateTodoItem(DESCRIPTION);

    var items = util.performGetTodoItems();

    assertEquals(1, items.size());
    assertTodoItem(items.getFirst(), DESCRIPTION, TodoStatusApi.NOT_DONE);
  }

  /** Helper method to assert to-do item fields. */
  public void assertTodoItem(
      TodoItemDto createdItem, String expectedDescription, TodoStatusApi expectedStatus) {

    assertNotNull(createdItem.id());
    assertEquals(expectedDescription, createdItem.description());
    assertEquals(expectedStatus, createdItem.status());
    assertTrue(createdItem.creationDatetime().isBefore(createdItem.dueDatetime()));
  }

    /** Helper method to create a to-do item and return the DTO. */
    private TodoItemDto performCreateTodoItem(String description) throws Exception {
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
    public List<TodoItemDto> performGetTodoItems() throws Exception {
        MvcResult result  = mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(
            responseBody,
            objectMapper.getTypeFactory().constructCollectionType(List.class, TodoItemDto.class));
    }
}
