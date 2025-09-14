package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
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