package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.*;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void shouldPublishCourseSuccessfully() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        OpenTextTask openTask = new OpenTextTask();
        openTask.setTaskOrder(1);
        openTask.setType(Type.OPEN_TEXT);

        SingleChoiceTask singleTask = new SingleChoiceTask();
        singleTask.setTaskOrder(2);
        singleTask.setType(Type.SINGLE_CHOICE);

        MultipleChoiceTask multipleTask = new MultipleChoiceTask();
        multipleTask.setTaskOrder(3);
        multipleTask.setType(Type.MULTIPLE_CHOICE);

        List<Task> tasks = Arrays.asList(openTask, singleTask, multipleTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseIdOrderByTaskOrder(1L)).thenReturn(tasks);

        courseService.publishCourse(1L);

        assertEquals(Status.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());
        verify(courseRepository).save(course);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.publishCourse(1L));
        
        assertEquals("Course not found", exception.getReason());
    }

    @Test
    void shouldThrowExceptionWhenCourseNotBuilding() {
        Course course = new Course();
        course.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.publishCourse(1L));
        
        assertEquals("Course can only be published if status is BUILDING", exception.getReason());
    }

    @Test
    void shouldThrowExceptionWhenMissingTaskTypes() {
        Course course = new Course();
        course.setStatus(Status.BUILDING);

        OpenTextTask openTask = new OpenTextTask();
        openTask.setTaskOrder(1);
        openTask.setType(Type.OPEN_TEXT);

        List<Task> tasks = Arrays.asList(openTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseIdOrderByTaskOrder(1L)).thenReturn(tasks);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.publishCourse(1L));
        
        assertEquals("Course must have at least one task of each type", exception.getReason());
    }

    @Test
    void shouldThrowExceptionWhenTaskOrderNotContinuous() {
        Course course = new Course();
        course.setStatus(Status.BUILDING);

        OpenTextTask openTask = new OpenTextTask();
        openTask.setTaskOrder(1);
        openTask.setType(Type.OPEN_TEXT);

        SingleChoiceTask singleTask = new SingleChoiceTask();
        singleTask.setTaskOrder(3); // Gap in sequence
        singleTask.setType(Type.SINGLE_CHOICE);

        MultipleChoiceTask multipleTask = new MultipleChoiceTask();
        multipleTask.setTaskOrder(4);
        multipleTask.setType(Type.MULTIPLE_CHOICE);

        List<Task> tasks = Arrays.asList(openTask, singleTask, multipleTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseIdOrderByTaskOrder(1L)).thenReturn(tasks);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.publishCourse(1L));
        
        assertEquals("Tasks must have continuous order sequence", exception.getReason());
    }
}