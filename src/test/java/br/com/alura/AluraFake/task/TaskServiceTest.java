package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.validation.MultipleChoiceValidator;
import br.com.alura.AluraFake.task.validation.SingleChoiceValidator;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskFactory taskFactory;

    @Mock
    private SingleChoiceValidator singleChoiceValidator;

    @Mock
    private MultipleChoiceValidator multipleChoiceValidator;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateOpenTextTaskSuccessfully() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(false);
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(null);
        when(taskFactory.createOpenTextTask(anyString(), anyInt(), any(Course.class))).thenReturn(mock(Task.class));

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
        };
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldRejectTaskWhenCourseNotBuilding() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        course.setStatus(Status.PUBLISHED);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
        };
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldRejectDuplicateStatement() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(true);

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 1; }
        };
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldRejectInvalidOrderSequence() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(false);
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(2);

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 5; }
        };
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldCreateSingleChoiceTaskSuccessfully() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(false);
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(null);
        when(singleChoiceValidator.validate(any())).thenReturn(null);
        when(taskFactory.createSingleChoiceTask(anyString(), anyInt(), any(Course.class), anyList())).thenReturn(mock(Task.class));

        NewSingleChoiceTaskDTO dto = mock(NewSingleChoiceTaskDTO.class);
        when(dto.getCourseId()).thenReturn(1L);
        when(dto.getStatement()).thenReturn("Test statement");
        when(dto.getOrder()).thenReturn(1);
        
        ResponseEntity<?> response = taskService.createSingleChoiceTask(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldCreateMultipleChoiceTaskSuccessfully() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(false);
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(null);
        when(multipleChoiceValidator.validate(any())).thenReturn(null);
        when(taskFactory.createMultipleChoiceTask(anyString(), anyInt(), any(Course.class), anyList())).thenReturn(mock(Task.class));

        NewMultipleChoiceTaskDTO dto = mock(NewMultipleChoiceTaskDTO.class);
        when(dto.getCourseId()).thenReturn(1L);
        when(dto.getStatement()).thenReturn("Test statement");
        when(dto.getOrder()).thenReturn(1);
        
        ResponseEntity<?> response = taskService.createMultipleChoiceTask(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldAdjustTaskOrdersWhenInsertingInMiddle() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        Task existingTask = mock(Task.class);
        when(existingTask.getOrderNumber()).thenReturn(2);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(false);
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(3);
        when(taskRepository.findByCourseIdAndOrderNumberGreaterThanEqual(1L, 2)).thenReturn(List.of(existingTask));
        when(taskFactory.createOpenTextTask(anyString(), anyInt(), any(Course.class))).thenReturn(mock(Task.class));

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO() {
            public Long getCourseId() { return 1L; }
            public String getStatement() { return "Test statement"; }
            public Integer getOrder() { return 2; }
        };
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(existingTask).setOrderNumber(3);
        verify(taskRepository).saveAll(anyList());
    }
}