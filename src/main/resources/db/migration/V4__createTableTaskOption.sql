CREATE TABLE task_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_text VARCHAR(80) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    task_id BIGINT NOT NULL,
    FOREIGN KEY (task_id) REFERENCES task(id)
);