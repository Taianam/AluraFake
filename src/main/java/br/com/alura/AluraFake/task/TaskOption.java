package br.com.alura.AluraFake.task;

import jakarta.persistence.*;

@Entity
@Table(name = "task_option")
public class TaskOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text", nullable = false, length = 80)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    protected TaskOption() {}

    public TaskOption(String optionText, Boolean isCorrect) {
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public String getOptionText() {
        return optionText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}