package br.com.alura.AluraFake.task.validation;

import br.com.alura.AluraFake.task.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.TaskOptionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SingleChoiceValidatorTest {

    private final SingleChoiceValidator validator = new SingleChoiceValidator();

    @Test
    void shouldValidateSuccessfully() {
        NewSingleChoiceTaskDTO dto = createValidDTO();
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertNull(result);
    }

    @Test
    void shouldRejectDuplicateOptions() {
        NewSingleChoiceTaskDTO dto = mock(NewSingleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Java", false);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2));
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldRejectMultipleCorrectOptions() {
        NewSingleChoiceTaskDTO dto = mock(NewSingleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Python", true);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2));
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    private NewSingleChoiceTaskDTO createValidDTO() {
        NewSingleChoiceTaskDTO dto = mock(NewSingleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Python", false);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2));
        return dto;
    }

    private TaskOptionDTO createOption(String text, boolean isCorrect) {
        TaskOptionDTO option = mock(TaskOptionDTO.class);
        when(option.getOption()).thenReturn(text);
        when(option.getIsCorrect()).thenReturn(isCorrect);
        return option;
    }
}