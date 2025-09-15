package br.com.alura.AluraFake.task.validation;

import br.com.alura.AluraFake.task.service.validation.MultipleChoiceValidator;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.TaskOptionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleChoiceValidatorTest {

    private final MultipleChoiceValidator validator = new MultipleChoiceValidator();

    @Test
    void shouldValidateSuccessfully() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Spring", true);
        TaskOptionRequest option3 = new TaskOptionRequest("Python", false);
        
        NewMultipleChoiceTaskRequest dto = new NewMultipleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2, option3)
        );
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertNull(result);
    }

    @Test
    void shouldRejectDuplicateOptions() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Java", false);
        TaskOptionRequest option3 = new TaskOptionRequest("Python", false);
        
        NewMultipleChoiceTaskRequest dto = new NewMultipleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2, option3)
        );
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldRejectOnlyOneCorrectOption() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Python", false);
        TaskOptionRequest option3 = new TaskOptionRequest("Ruby", false);
        
        NewMultipleChoiceTaskRequest dto = new NewMultipleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2, option3)
        );
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldRejectAllCorrectOptions() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Spring", true);
        TaskOptionRequest option3 = new TaskOptionRequest("Hibernate", true);
        
        NewMultipleChoiceTaskRequest dto = new NewMultipleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2, option3)
        );
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}