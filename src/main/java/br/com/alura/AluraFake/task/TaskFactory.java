package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskFactory {

    public Task createOpenTextTask(String statement, Integer order, Course course) {
        return new OpenTextTask(statement, order, course);
    }

    public Task createSingleChoiceTask(String statement, Integer order, Course course, List<TaskOptionDTO> optionDTOs) {
        List<TaskOption> options = optionDTOs.stream()
                .map(dto -> new TaskOption(dto.getOption(), dto.getIsCorrect()))
                .collect(Collectors.toList());
        return new SingleChoiceTask(statement, order, course, options);
    }

    public Task createMultipleChoiceTask(String statement, Integer order, Course course, List<TaskOptionDTO> optionDTOs) {
        List<TaskOption> options = optionDTOs.stream()
                .map(dto -> new TaskOption(dto.getOption(), dto.getIsCorrect()))
                .collect(Collectors.toList());
        return new MultipleChoiceTask(statement, order, course, options);
    }
}