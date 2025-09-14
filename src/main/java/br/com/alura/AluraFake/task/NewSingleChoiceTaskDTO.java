package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class NewSingleChoiceTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Positive
    private Integer order;

    @Valid
    @NotNull
    @Size(min = 2, max = 5)
    private List<TaskOptionDTO> options;

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public List<TaskOptionDTO> getOptions() {
        return options;
    }
}