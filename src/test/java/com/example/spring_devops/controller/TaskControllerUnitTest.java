package com.example.spring_devops.controller;


import com.example.spring_devops.model.Task;
import com.example.spring_devops.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.is;




@WebMvcTest(TaskController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    Task task;

    @BeforeEach
    public void setup() {
        task = Task.builder()
                .id(1L)
                .name("task one")
                .description("test controller for task one")
                .deadline(LocalDate.parse("2024-05-02"))
                .build();
    }


    //Post Controller
    @Test
    @Order(1)
    public void saveTaskTest() throws Exception{
        // precondition
        given(taskService.createTask(any(Task.class))).willReturn(task);

        // action
        ResultActions response = mockMvc.perform(post("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        // verify
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(task.getName())))
                .andExpect(jsonPath("$.description", is(task.getDescription())))
                .andExpect(jsonPath("$.deadline", is(task.getDeadline().toString()))); // Convert LocalDate to String

    }

    //Get Controller
    @Test
    @Order(2)
    public void getTaskTest() throws Exception{
        // precondition
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        taskList.add(Task.builder().id(2L).name("task one").description("test controller for task one").deadline(LocalDate.parse("2024-05-02")).build());
        given(taskService.getAllTasks()).willReturn(taskList);

        // action
        ResultActions response = mockMvc.perform(get("/api/task"));

        // verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(taskList.size())));

    }

    //get by  task Id controller
    @Test
    @Order(3)
    public void getByIdTaskTest() throws Exception{
        // precondition
        given(taskService.getTaskById(task.getId())).willReturn(Optional.of(task));

        // action
        ResultActions response = mockMvc.perform(get("/api/task/{id}", task.getId()));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(task.getName())))
                .andExpect(jsonPath("$.description", is(task.getDescription())))
                .andExpect(jsonPath("$.deadline", is(task.getDeadline().toString()))); // Convert LocalDate to String

    }





    // delete task
    @Test
    @Order(4)
    public void deleteTaskTest() throws Exception{
        // precondition
        willDoNothing().given(taskService).deleteTask(task.getId());

        // action
        ResultActions response = mockMvc.perform(delete("/api/task/{id}", task.getId()));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }
}
