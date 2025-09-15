package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.entity.Status;
import br.com.alura.AluraFake.task.service.validation.MultipleChoiceValidator;
import br.com.alura.AluraFake.task.service.validation.SingleChoiceValidator;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.task.service.TaskFactory;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.entity.*;
import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.user.entity.Role;
import br.com.alura.AluraFake.user.entity.User;
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
        
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.getStatement()).thenReturn("Test statement");
        when(mockTask.getOrderNumber()).thenReturn(1);
        when(mockTask.getType()).thenReturn(Type.OPEN_TEXT);
        when(mockTask.getCourse()).thenReturn(course);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(false);
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(null);
        when(taskFactory.createOpenTextTask(anyString(), anyInt(), any(Course.class))).thenReturn(mockTask);
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(1L, "Test statement", 1);
        
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

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(1L, "Test statement", 1);
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldRejectDuplicateStatement() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);
        Course course = new Course("Test Course", "Description", instructor);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Test statement")).thenReturn(true);

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(1L, "Test statement", 1);
        
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

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(1L, "Test statement", 5);
        
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
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.getCourse()).thenReturn(course);
        when(taskFactory.createSingleChoiceTask(anyString(), anyInt(), any(Course.class), anyList())).thenReturn(mockTask);
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        NewSingleChoiceTaskRequest dto = new NewSingleChoiceTaskRequest(1L, "Test statement", 1, List.of());
        
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
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.getCourse()).thenReturn(course);
        when(taskFactory.createMultipleChoiceTask(anyString(), anyInt(), any(Course.class), anyList())).thenReturn(mockTask);
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        NewMultipleChoiceTaskRequest dto = new NewMultipleChoiceTaskRequest(1L, "Test statement", 1, List.of());
        
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
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.getCourse()).thenReturn(course);
        when(taskFactory.createOpenTextTask(anyString(), anyInt(), any(Course.class))).thenReturn(mockTask);
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        NewOpenTextTaskRequest dto = new NewOpenTextTaskRequest(1L, "Test statement", 2);
        
        ResponseEntity<?> response = taskService.createOpenTextTask(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(existingTask).setOrderNumber(3);
        verify(taskRepository).saveAll(anyList());
    }
}