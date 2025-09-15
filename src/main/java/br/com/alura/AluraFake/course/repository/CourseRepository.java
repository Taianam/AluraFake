package br.com.alura.AluraFake.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.AluraFake.course.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>{

    List<Course> findByInstructorId(Long instructorId);

}
