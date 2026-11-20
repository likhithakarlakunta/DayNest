package com.daynest.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private LocalDate entryDate;
    @Column(length = 5000)
    private String content;
    private String mood;   // optional

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
}
