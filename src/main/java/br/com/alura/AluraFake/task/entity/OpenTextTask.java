package br.com.alura.AluraFake.task.entity;

import br.com.alura.AluraFake.course.entity.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {

    public OpenTextTask() {}

    public OpenTextTask(String statement, Integer orderNumber, Course course) {
        super(statement, orderNumber, course, Type.OPEN_TEXT);
    }
}
