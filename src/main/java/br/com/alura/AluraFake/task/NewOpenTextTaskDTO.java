package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.*;

public class NewOpenTextTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Positive
    private Integer order;

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }
}