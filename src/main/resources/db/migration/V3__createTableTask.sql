CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    statement VARCHAR(255) NOT NULL,
    order_number INT NOT NULL,
    course_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(id),
    UNIQUE KEY unique_course_statement (course_id, statement),
    UNIQUE KEY unique_course_order (course_id, order_number)
);