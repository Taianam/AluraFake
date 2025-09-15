package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.task.entity.*;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.task.dto.TaskOptionRequest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


@Component
public class TaskFactory {

    public Task createOpenTextTask(String statement, Integer order, Course course) {
        return new OpenTextTask(statement, order, course);
    }

    public Task createSingleChoiceTask(String statement, Integer order, Course course, List<TaskOptionRequest> optionRequests) {
        List<TaskOption> options = optionRequests.stream()
                .map(req -> new TaskOption(req.option(), req.isCorrect()))
                .collect(Collectors.toList());
        return new SingleChoiceTask(statement, order, course, options);
    }

    public Task createMultipleChoiceTask(String statement, Integer order, Course course, List<TaskOptionRequest> optionRequests) {
        List<TaskOption> options = optionRequests.stream()
                .map(req -> new TaskOption(req.option(), req.isCorrect()))
                .collect(Collectors.toList());
        return new MultipleChoiceTask(statement, order, course, options);
    }
}