package br.com.alura.AluraFake.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
    boolean existsByCourseIdAndStatement(Long courseId, String statement);
    
    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId AND t.orderNumber >= :order ORDER BY t.orderNumber")
    List<Task> findByCourseIdAndOrderNumberGreaterThanEqual(@Param("courseId") Long courseId, @Param("order") Integer order);
    
    @Query("SELECT MAX(t.orderNumber) FROM Task t WHERE t.course.id = :courseId")
    Integer findMaxOrderByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(DISTINCT t.type) FROM Task t WHERE t.course.id = :courseId")
    Long countDistinctTypesByCourseId(@Param("courseId") Long courseId);
    
    List<Task> findByCourseIdOrderByOrderNumber(Long courseId);
    
    List<Task> findByCourseIdOrderByTaskOrder(Long courseId);
    
    Long countByCourseId(Long courseId);
}