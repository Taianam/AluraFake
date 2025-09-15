package br.com.alura.AluraFake.task.controller;

import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.task.dto.*;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity<?> newOpenTextExercise(@Valid @RequestBody NewOpenTextTaskRequest dto) {
        return taskService.createOpenTextTask(dto);
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity<?> newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskRequest dto) {
        return taskService.createSingleChoiceTask(dto);
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity<?> newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskRequest dto) {
        return taskService.createMultipleChoiceTask(dto);
    }
}