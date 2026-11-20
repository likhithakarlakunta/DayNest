package com.daynest.controller;

import com.daynest.model.TaskList;
import com.daynest.repository.TaskListRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class TaskListController {
    private final TaskListRepository repo;
    public TaskListController(TaskListRepository repo) { this.repo = repo; }

    @GetMapping public List<TaskList> all() { return repo.findAll(); }

    @PostMapping public TaskList create(@Valid @RequestBody TaskList l) { l.setId(null); return repo.save(l); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
