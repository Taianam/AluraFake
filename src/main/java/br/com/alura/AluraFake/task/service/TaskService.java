package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.task.entity.*;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.entity.Status;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.validation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.AluraFake.util.ErrorItemDTO;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final TaskFactory taskFactory;
    private final SingleChoiceValidator singleChoiceValidator;
    private final MultipleChoiceValidator multipleChoiceValidator;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository, 
                      TaskFactory taskFactory, SingleChoiceValidator singleChoiceValidator,
                      MultipleChoiceValidator multipleChoiceValidator) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.taskFactory = taskFactory;
        this.singleChoiceValidator = singleChoiceValidator;
        this.multipleChoiceValidator = multipleChoiceValidator;
    }

    @Transactional
    public ResponseEntity<?> createOpenTextTask(NewOpenTextTaskRequest dto) {
        ResponseEntity<?> validationResult = validateTaskCreation(dto.courseId(), dto.statement(), dto.order());
        if (validationResult != null) return validationResult;

        Course course = courseRepository.findById(dto.courseId()).get();
        adjustTaskOrders(dto.courseId(), dto.order());
        
        Task task = taskFactory.createOpenTextTask(dto.statement(), dto.order(), course);
        Task savedTask = taskRepository.save(task);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(savedTask));
    }

    @Transactional
    public ResponseEntity<?> createSingleChoiceTask(NewSingleChoiceTaskRequest dto) {
        ResponseEntity<?> validationResult = validateTaskCreation(dto.courseId(), dto.statement(), dto.order());
        if (validationResult != null) return validationResult;

        ResponseEntity<?> optionsValidation = singleChoiceValidator.validate(dto);
        if (optionsValidation != null) return optionsValidation;

        Course course = courseRepository.findById(dto.courseId()).get();
        adjustTaskOrders(dto.courseId(), dto.order());

        Task task = taskFactory.createSingleChoiceTask(dto.statement(), dto.order(), course, dto.options());
        Task savedTask = taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(savedTask));
    }

    @Transactional
    public ResponseEntity<?> createMultipleChoiceTask(NewMultipleChoiceTaskRequest dto) {
        ResponseEntity<?> validationResult = validateTaskCreation(dto.courseId(), dto.statement(), dto.order());
        if (validationResult != null) return validationResult;

        ResponseEntity<?> optionsValidation = multipleChoiceValidator.validate(dto);
        if (optionsValidation != null) return optionsValidation;

        Course course = courseRepository.findById(dto.courseId()).get();
        adjustTaskOrders(dto.courseId(), dto.order());

        Task task = taskFactory.createMultipleChoiceTask(dto.statement(), dto.order(), course, dto.options());
        Task savedTask = taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(savedTask));
    }

    private ResponseEntity<?> validateTaskCreation(Long courseId, String statement, Integer order) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", "Curso não encontrado"));
        }

        Course course = courseOpt.get();
        if (course.getStatus() != Status.BUILDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", "Curso deve estar com status BUILDING"));
        }

        if (taskRepository.existsByCourseIdAndStatement(courseId, statement)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("statement", "Enunciado já existe neste curso"));
        }

        Integer maxOrder = taskRepository.findMaxOrderByCourseId(courseId);
        if (maxOrder != null && order > maxOrder + 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("order", "Sequência de ordem deve ser contínua"));
        }

        return null;
    }

    private void adjustTaskOrders(Long courseId, Integer newOrder) {
        List<Task> tasksToAdjust = taskRepository.findByCourseIdAndOrderNumberGreaterThanEqual(courseId, newOrder);
        tasksToAdjust.forEach(task -> task.setOrderNumber(task.getOrderNumber() + 1));
        taskRepository.saveAll(tasksToAdjust);
    }
}