package br.com.alura.AluraFake.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import br.com.alura.AluraFake.task.entity.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.entity.Type;
import br.com.alura.AluraFake.user.entity.Role;
import br.com.alura.AluraFake.user.entity.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.course.entity.*;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.dto.InstructorCoursesReportDTO;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void shouldPublishCourseSuccessfully() {
        Course course = new Course();
        course.setStatus(Status.BUILDING);

        Task openTask = mock(Task.class);
        when(openTask.getOrderNumber()).thenReturn(1);
        when(openTask.getType()).thenReturn(Type.OPEN_TEXT);

        Task singleTask = mock(Task.class);
        when(singleTask.getOrderNumber()).thenReturn(2);
        when(singleTask.getType()).thenReturn(Type.SINGLE_CHOICE);

        Task multipleTask = mock(Task.class);
        when(multipleTask.getOrderNumber()).thenReturn(3);
        when(multipleTask.getType()).thenReturn(Type.MULTIPLE_CHOICE);

        // Garantir que as tasks estejam na ordem correta (1, 2, 3)
        List<Task> tasks = Arrays.asList(openTask, singleTask, multipleTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseIdOrderByOrderNumber(1L)).thenReturn(tasks);

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

        Task openTask = mock(Task.class);
        when(openTask.getType()).thenReturn(Type.OPEN_TEXT);

        List<Task> tasks = Arrays.asList(openTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseIdOrderByOrderNumber(1L)).thenReturn(tasks);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.publishCourse(1L));
        
        assertEquals("Course must have at least one task of each type", exception.getReason());
    }

    @Test
    void shouldGenerateInstructorReportSuccessfully() {
        User instructor = new User("Test", "test@test.com", Role.INSTRUCTOR);

        Course course1 = new Course();
        course1.setStatus(Status.PUBLISHED);

        Course course2 = new Course();
        course2.setStatus(Status.BUILDING);

        List<Course> courses = Arrays.asList(course1, course2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByInstructorId(1L)).thenReturn(courses);
        when(taskRepository.countByCourseId(any())).thenReturn(3L);

        InstructorCoursesReportDTO result = courseService.getInstructorCoursesReport(1L);

        assertEquals(2, result.getCourses().size());
        assertEquals(1, result.getTotalPublishedCourses());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForReport() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.getInstructorCoursesReport(1L));
        
        assertEquals("User not found", exception.getReason());
    }

    @Test
    void shouldThrowExceptionWhenUserNotInstructor() {
        User user = new User("Test", "test@test.com", Role.STUDENT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> courseService.getInstructorCoursesReport(1L));
        
        assertEquals("User is not an instructor", exception.getReason());
    }
}