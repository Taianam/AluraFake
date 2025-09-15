package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.entity.*;

public record TaskResponse(
    Long id,
    String statement,
    Integer orderNumber,
    Type type,
    Long courseId
) {
    public TaskResponse(Task task) {
        this(
            task.getId(),
            task.getStatement(),
            task.getOrderNumber(),
            task.getType(),
            task.getCourse().getId()
        );
    }
}