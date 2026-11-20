package com.daynest.repository;

import com.daynest.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Single query handles every optional filter (null = ignore that filter)
    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:dueDate IS NULL OR t.dueDate = :dueDate) " +
           "ORDER BY t.dueDate ASC")
    List<Task> filter(@Param("status") Task.Status status,
                      @Param("priority") Task.Priority priority,
                      @Param("category") String category,
                      @Param("dueDate") LocalDate dueDate);

    List<Task> findByDueDateBetweenAndStatusNot(LocalDate from, LocalDate to, Task.Status status);
    List<Task> findByDueDateBeforeAndStatusNot(LocalDate date, Task.Status status);
    long countByStatus(Task.Status status);
}
