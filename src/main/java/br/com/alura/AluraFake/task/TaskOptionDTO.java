package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.*;

public class TaskOptionDTO {

    @NotBlank
    @Size(min = 4, max = 80)
    private String option;

    @NotNull
    private Boolean isCorrect;

    public String getOption() {
        return option;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }
}