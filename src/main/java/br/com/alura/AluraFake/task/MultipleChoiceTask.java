package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import java.util.List;


@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends Task {

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskOption> options;

    protected MultipleChoiceTask() {}

    public MultipleChoiceTask(String statement, Integer orderNumber, Course course, List<TaskOption> options) {
        super(statement, orderNumber, course, Type.MULTIPLE_CHOICE);
        this.options = options;
        options.forEach(option -> option.setTask(this));
    }

    public List<TaskOption> getOptions() {
        return options;
    }
}