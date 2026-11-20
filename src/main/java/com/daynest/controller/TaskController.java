package com.daynest.controller;

import com.daynest.model.Task;
import com.daynest.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository repo;
    public TaskController(TaskRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Task> list(@RequestParam(required = false) Task.Status status,
                           @RequestParam(required = false) Task.Priority priority,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return repo.filter(status, priority, (category == null || category.isBlank()) ? null : category, dueDate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task create(@Valid @RequestBody Task task) {
        task.setId(null);
        return repo.save(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @Valid @RequestBody Task in) {
        return repo.findById(id).map(t -> {
            t.setTitle(in.getTitle());
            t.setDescription(in.getDescription());
            t.setStatus(in.getStatus());
            t.setPriority(in.getPriority());
            t.setCategory(in.getCategory());
            t.setDueDate(in.getDueDate());
            t.setReminderTime(in.getReminderTime());
            t.setListId(in.getListId());
            return ResponseEntity.ok(repo.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upcoming")
    public List<Task> upcoming() {
        LocalDate today = LocalDate.now();
        return repo.findByDueDateBetweenAndStatusNot(today, today.plusDays(7), Task.Status.COMPLETED);
    }

    @GetMapping("/overdue")
    public List<Task> overdue() {
        return repo.findByDueDateBeforeAndStatusNot(LocalDate.now(), Task.Status.COMPLETED);
    }
}
