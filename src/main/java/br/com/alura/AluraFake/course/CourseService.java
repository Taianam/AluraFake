package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (course.getStatus() != Status.BUILDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course can only be published if status is BUILDING");
        }

        List<Task> tasks = taskRepository.findByCourseIdOrderByOrderNumber(courseId);

        if (tasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course must have at least one task of each type");
        }

        Set<Type> taskTypes = tasks.stream().map(Task::getType).collect(Collectors.toSet());
        if (!taskTypes.containsAll(Set.of(Type.OPEN_TEXT, Type.SINGLE_CHOICE, Type.MULTIPLE_CHOICE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course must have at least one task of each type");
        }

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskOrder() != i + 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tasks must have continuous order sequence");
            }
        }

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    public InstructorCoursesReportDTO getInstructorCoursesReport(Long instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an instructor");
        }

        List<Course> courses = courseRepository.findByInstructorId(instructorId);
        
        List<InstructorCoursesReportDTO.CourseReportItem> courseItems = courses.stream()
                .map(course -> {
                    int taskCount = taskRepository.countByCourseId(course.getId()).intValue();
                    return new InstructorCoursesReportDTO.CourseReportItem(
                            course.getId(),
                            course.getTitle(),
                            course.getStatus(),
                            course.getPublishedAt(),
                            taskCount
                    );
                })
                .collect(Collectors.toList());

        long publishedCoursesCount = courses.stream()
                .filter(course -> course.getStatus() == Status.PUBLISHED)
                .count();

        return new InstructorCoursesReportDTO(courseItems, publishedCoursesCount);
    }
}