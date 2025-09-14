package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.validation.MultipleChoiceValidator;
import br.com.alura.AluraFake.task.validation.SingleChoiceValidator;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> createOpenTextTask(NewOpenTextTaskDTO dto) {
        ResponseEntity<?> validationResult = validateTaskCreation(dto.getCourseId(), dto.getStatement(), dto.getOrder());
        if (validationResult != null) return validationResult;

        Course course = courseRepository.findById(dto.getCourseId()).get();
        adjustTaskOrders(dto.getCourseId(), dto.getOrder());
        
        Task task = taskFactory.createOpenTextTask(dto.getStatement(), dto.getOrder(), course);
        taskRepository.save(task);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    public ResponseEntity<?> createSingleChoiceTask(NewSingleChoiceTaskDTO dto) {
        ResponseEntity<?> validationResult = validateTaskCreation(dto.getCourseId(), dto.getStatement(), dto.getOrder());
        if (validationResult != null) return validationResult;

        ResponseEntity<?> optionsValidation = singleChoiceValidator.validate(dto);
        if (optionsValidation != null) return optionsValidation;

        Course course = courseRepository.findById(dto.getCourseId()).get();
        adjustTaskOrders(dto.getCourseId(), dto.getOrder());

        Task task = taskFactory.createSingleChoiceTask(dto.getStatement(), dto.getOrder(), course, dto.getOptions());
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    public ResponseEntity<?> createMultipleChoiceTask(NewMultipleChoiceTaskDTO dto) {
        ResponseEntity<?> validationResult = validateTaskCreation(dto.getCourseId(), dto.getStatement(), dto.getOrder());
        if (validationResult != null) return validationResult;

        ResponseEntity<?> optionsValidation = multipleChoiceValidator.validate(dto);
        if (optionsValidation != null) return optionsValidation;

        Course course = courseRepository.findById(dto.getCourseId()).get();
        adjustTaskOrders(dto.getCourseId(), dto.getOrder());

        Task task = taskFactory.createMultipleChoiceTask(dto.getStatement(), dto.getOrder(), course, dto.getOptions());
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private ResponseEntity<?> validateTaskCreation(Long courseId, String statement, Integer order) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Course course = courseOpt.get();
        if (course.getStatus() != Status.BUILDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", "Course must be in BUILDING status"));
        }

        if (taskRepository.existsByCourseIdAndStatement(courseId, statement)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("statement", "Statement already exists in this course"));
        }

        Integer maxOrder = taskRepository.findMaxOrderByCourseId(courseId);
        if (maxOrder != null && order > maxOrder + 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("order", "Order sequence must be continuous"));
        }

        return null;
    }

    private ResponseEntity<?> validateSingleChoiceOptions(List<TaskOptionDTO> options, String statement) {
        Set<String> optionTexts = options.stream().map(TaskOptionDTO::getOption).collect(Collectors.toSet());
        
        if (optionTexts.size() != options.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Options must be unique"));
        }

        if (optionTexts.contains(statement)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Options cannot be equal to statement"));
        }

        long correctCount = options.stream().mapToLong(opt -> opt.getIsCorrect() ? 1 : 0).sum();
        if (correctCount != 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Must have exactly one correct option"));
        }

        return null;
    }

    private ResponseEntity<?> validateMultipleChoiceOptions(List<TaskOptionDTO> options, String statement) {
        Set<String> optionTexts = options.stream().map(TaskOptionDTO::getOption).collect(Collectors.toSet());
        
        if (optionTexts.size() != options.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Options must be unique"));
        }

        if (optionTexts.contains(statement)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Options cannot be equal to statement"));
        }

        long correctCount = options.stream().mapToLong(opt -> opt.getIsCorrect() ? 1 : 0).sum();
        long incorrectCount = options.size() - correctCount;
        
        if (correctCount < 2 || incorrectCount < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Must have at least 2 correct and 1 incorrect option"));
        }

        return null;
    }

    private void adjustTaskOrders(Long courseId, Integer newOrder) {
        List<Task> tasksToAdjust = taskRepository.findByCourseIdAndOrderNumberGreaterThanEqual(courseId, newOrder);
        tasksToAdjust.forEach(task -> task.setOrderNumber(task.getOrderNumber() + 1));
        taskRepository.saveAll(tasksToAdjust);
    }
}