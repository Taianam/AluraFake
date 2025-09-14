package br.com.alura.AluraFake.task.validation;

import br.com.alura.AluraFake.task.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.TaskOptionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MultipleChoiceValidatorTest {

    private final MultipleChoiceValidator validator = new MultipleChoiceValidator();

    @Test
    void shouldValidateSuccessfully() {
        NewMultipleChoiceTaskDTO dto = createValidDTO();
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertNull(result);
    }

    @Test
    void shouldRejectDuplicateOptions() {
        NewMultipleChoiceTaskDTO dto = mock(NewMultipleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Java", false);
        TaskOptionDTO option3 = createOption("Ruby", false);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2, option3));
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldRejectOnlyOneCorrectOption() {
        NewMultipleChoiceTaskDTO dto = mock(NewMultipleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Python", false);
        TaskOptionDTO option3 = createOption("Ruby", false);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2, option3));
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldRejectAllCorrectOptions() {
        NewMultipleChoiceTaskDTO dto = mock(NewMultipleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Python", true);
        TaskOptionDTO option3 = createOption("Ruby", true);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2, option3));
        
        ResponseEntity<?> result = validator.validate(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    private NewMultipleChoiceTaskDTO createValidDTO() {
        NewMultipleChoiceTaskDTO dto = mock(NewMultipleChoiceTaskDTO.class);
        when(dto.getStatement()).thenReturn("Test statement");
        
        TaskOptionDTO option1 = createOption("Java", true);
        TaskOptionDTO option2 = createOption("Spring", true);
        TaskOptionDTO option3 = createOption("Ruby", false);
        
        when(dto.getOptions()).thenReturn(Arrays.asList(option1, option2, option3));
        return dto;
    }

    private TaskOptionDTO createOption(String text, boolean isCorrect) {
        TaskOptionDTO option = mock(TaskOptionDTO.class);
        when(option.getOption()).thenReturn(text);
        when(option.getIsCorrect()).thenReturn(isCorrect);
        return option;
    }
}