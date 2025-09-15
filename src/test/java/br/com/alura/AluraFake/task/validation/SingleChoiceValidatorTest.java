package br.com.alura.AluraFake.task.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.TaskOptionRequest;
import br.com.alura.AluraFake.task.service.validation.SingleChoiceValidator;

class SingleChoiceValidatorTest {

    private final SingleChoiceValidator validator = new SingleChoiceValidator();

    @Test
    void shouldValidateSuccessfully() {
        NewSingleChoiceTaskRequest dto = createValidDTO();
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertNull(result);
    }

    @Test
    void shouldRejectDuplicateOptions() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Java", false);
        
        NewSingleChoiceTaskRequest dto = new NewSingleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2)
        );
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldRejectMultipleCorrectOptions() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Python", true);
        
        NewSingleChoiceTaskRequest dto = new NewSingleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2)
        );
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    private NewSingleChoiceTaskRequest createValidDTO() {
        TaskOptionRequest option1 = new TaskOptionRequest("Java", true);
        TaskOptionRequest option2 = new TaskOptionRequest("Python", false);
        
        return new NewSingleChoiceTaskRequest(
            1L, "Test statement", 1, List.of(option1, option2)
        );
    }
}