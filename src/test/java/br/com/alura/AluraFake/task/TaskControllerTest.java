package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

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

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
        };

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createSingleChoiceTask__should_return_created_when_valid() throws Exception {
        when(taskService.createSingleChoiceTask(any())).thenReturn(ResponseEntity.status(201).build());

        NewSingleChoiceTaskDTO dto = createValidSingleChoiceDTO();

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createMultipleChoiceTask__should_return_created_when_valid() throws Exception {
        when(taskService.createMultipleChoiceTask(any())).thenReturn(ResponseEntity.status(201).build());

        NewMultipleChoiceTaskDTO dto = createValidMultipleChoiceDTO();

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createOpenTextTask__should_return_bad_request_when_invalid() throws Exception {
        when(taskService.createOpenTextTask(any())).thenReturn(ResponseEntity.badRequest().build());

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 999L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
        };

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    private NewSingleChoiceTaskDTO createValidSingleChoiceDTO() {
        return new NewSingleChoiceTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
            public java.util.List<TaskOptionDTO> getOptions() {
                TaskOptionDTO option1 = new TaskOptionDTO() {
                    public String getOption() { return "Java"; }
                    public Boolean getIsCorrect() { return true; }
                };
                TaskOptionDTO option2 = new TaskOptionDTO() {
                    public String getOption() { return "Python"; }
                    public Boolean getIsCorrect() { return false; }
                };
                return Arrays.asList(option1, option2);
            }
        };
    }

    private NewMultipleChoiceTaskDTO createValidMultipleChoiceDTO() {
        return new NewMultipleChoiceTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
            public java.util.List<TaskOptionDTO> getOptions() {
                TaskOptionDTO option1 = new TaskOptionDTO() {
                    public String getOption() { return "Java"; }
                    public Boolean getIsCorrect() { return true; }
                };
                TaskOptionDTO option2 = new TaskOptionDTO() {
                    public String getOption() { return "Spring"; }
                    public Boolean getIsCorrect() { return true; }
                };
                TaskOptionDTO option3 = new TaskOptionDTO() {
                    public String getOption() { return "Ruby"; }
                    public Boolean getIsCorrect() { return false; }
                };
                return Arrays.asList(option1, option2, option3);
            }
        };
    }
}