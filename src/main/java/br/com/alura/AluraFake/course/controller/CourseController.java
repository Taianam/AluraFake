package br.com.alura.AluraFake.course.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.AluraFake.course.dto.CourseResponse;
import br.com.alura.AluraFake.course.dto.InstructorCoursesReportDTO;
import br.com.alura.AluraFake.course.dto.NewCourseRequest;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.entity.Status;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.user.entity.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository, CourseService courseService){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseRequest newCourse) {

        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.emailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
        }

        Course course = new Course(newCourse.title(), newCourse.description(), possibleAuthor.get());
        Course savedCourse = courseRepository.save(course);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new CourseResponse(savedCourse));
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseResponse>> listAllCourses() {
        List<CourseResponse> courses = courseRepository.findAll().stream()
                .map(CourseResponse::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/course/{id}/publish")
    public ResponseEntity publishCourse(@PathVariable("id") Long id) {
        try {
            courseService.publishCourse(id);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ErrorItemDTO("courseId", e.getReason()));
        }
    }

    @GetMapping("/instructor/{id}/courses")
    public ResponseEntity<?> getInstructorCourses(@PathVariable("id") Long id) {
        try {
            InstructorCoursesReportDTO report = courseService.getInstructorCoursesReport(id);
            return ResponseEntity.ok(report);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ErrorItemDTO("instructorId", e.getReason()));
        }
    }

}
