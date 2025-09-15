package br.com.alura.AluraFake.course.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.alura.AluraFake.course.entity.Status;

public class InstructorCoursesReportDTO {
    private List<CourseReportItem> courses;
    private long totalPublishedCourses;

    public InstructorCoursesReportDTO(List<CourseReportItem> courses, long totalPublishedCourses) {
        this.courses = courses;
        this.totalPublishedCourses = totalPublishedCourses;
    }

    public List<CourseReportItem> getCourses() {
        return courses;
    }

    public long getTotalPublishedCourses() {
        return totalPublishedCourses;
    }

    public static class CourseReportItem {
        private Long id;
        private String title;
        private Status status;
        private LocalDateTime publishedAt;
        private int taskCount;

        public CourseReportItem(Long id, String title, Status status, LocalDateTime publishedAt, int taskCount) {
            this.id = id;
            this.title = title;
            this.status = status;
            this.publishedAt = publishedAt;
            this.taskCount = taskCount;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Status getStatus() {
            return status;
        }

        public LocalDateTime getPublishedAt() {
            return publishedAt;
        }

        public int getTaskCount() {
            return taskCount;
        }
    }
}