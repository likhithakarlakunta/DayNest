package com.daynest.controller;

import com.daynest.model.JournalEntry;
import com.daynest.repository.JournalRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journal")
public class JournalController {
    private final JournalRepository repo;
    public JournalController(JournalRepository repo) { this.repo = repo; }

    @GetMapping public List<JournalEntry> all() { return repo.findAll(); }

    @GetMapping("/{date}")
    public ResponseEntity<JournalEntry> byDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByEntryDate(date).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // One entry per date: create it, or overwrite the existing one (upsert)
    @PutMapping("/{date}")
    public JournalEntry save(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestBody JournalEntry in) {
        JournalEntry e = repo.findByEntryDate(date).orElseGet(JournalEntry::new);
        e.setEntryDate(date);
        e.setContent(in.getContent());
        e.setMood(in.getMood());
        return repo.save(e);
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> delete(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByEntryDate(date).map(e -> { repo.delete(e); return ResponseEntity.noContent().<Void>build(); })
                   .orElse(ResponseEntity.notFound().build());
    }
}
