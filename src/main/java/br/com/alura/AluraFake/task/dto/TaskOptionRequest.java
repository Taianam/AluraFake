package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.*;

public record TaskOptionRequest(
    @NotBlank @Size(min = 4, max = 80) String option,
    @NotNull Boolean isCorrect
) {}
