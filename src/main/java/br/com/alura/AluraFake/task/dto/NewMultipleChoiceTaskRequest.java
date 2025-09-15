package br.com.alura.AluraFake.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record NewMultipleChoiceTaskRequest(
    @NotNull Long courseId,
    @NotBlank @Size(min = 4, max = 255) String statement,
    @NotNull @Positive Integer order,
    @Valid @NotNull @Size(min = 3, max = 5) List<TaskOptionRequest> options
) {}
