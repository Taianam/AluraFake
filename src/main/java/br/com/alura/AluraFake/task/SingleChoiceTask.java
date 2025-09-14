package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import java.util.List;


@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends Task {

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskOption> options;

    public SingleChoiceTask() {}

    public SingleChoiceTask(String statement, Integer orderNumber, Course course, List<TaskOption> options) {
        super(statement, orderNumber, course, Type.SINGLE_CHOICE);
        this.options = options;
        options.forEach(option -> option.setTask(this));
    }

    public List<TaskOption> getOptions() {
        return options;
    }
}