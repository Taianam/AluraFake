package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.AluraFake.task.controller.TaskController;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.task.dto.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOpenTextTask__should_return_created_when_valid() throws Exception {
        when(taskService.createOpenTextTask(any())).thenReturn(ResponseEntity.status(201).build());

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(1L, "Test statement", 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createSingleChoiceTask__should_return_created_when_valid() throws Exception {
        when(taskService.createSingleChoiceTask(any())).thenReturn(ResponseEntity.status(201).build());

        NewSingleChoiceTaskRequest dto = createValidSingleChoiceDTO();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createMultipleChoiceTask__should_return_created_when_valid() throws Exception {
        when(taskService.createMultipleChoiceTask(any())).thenReturn(ResponseEntity.status(201).build());

        NewMultipleChoiceTaskRequest dto = createValidMultipleChoiceDTO();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createOpenTextTask__should_return_bad_request_when_invalid() throws Exception {
        when(taskService.createOpenTextTask(any())).thenReturn(ResponseEntity.badRequest().build());

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(999L, "Test statement", 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    private NewSingleChoiceTaskRequest createValidSingleChoiceDTO() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Python", false);
        
        return new NewSingleChoiceTaskRequest(1L, "Test statement", 1, List.of(option1, option2));
    }

    private NewMultipleChoiceTaskRequest createValidMultipleChoiceDTO() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Spring", true);
        TaskOptionRequest option3 = new TaskOptionRequest("Ruby", false);
        
        return new NewMultipleChoiceTaskRequest(1L, "Test statement", 1, List.of(option1, option2, option3));
    }
}