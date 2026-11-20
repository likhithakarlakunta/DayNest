package com.daynest.controller;

import com.daynest.model.Note;
import com.daynest.repository.NoteRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteRepository repo;
    public NoteController(NoteRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Note> list(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        return repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q);
    }

    @PostMapping public Note create(@Valid @RequestBody Note n) { n.setId(null); return repo.save(n); }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @Valid @RequestBody Note in) {
        return repo.findById(id).map(n -> {
            n.setTitle(in.getTitle());
            n.setContent(in.getContent());
            return ResponseEntity.ok(repo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
