package br.com.alura.AluraFake.course.dto;

import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.entity.Status;

public record CourseResponse(
    Long id,
    String title,
    String description,
    Status status
) {
    public CourseResponse(Course course) {
        this(
            course.getId(),
            course.getTitle(),
            course.getDescription(),
            course.getStatus()
        );
    }
}