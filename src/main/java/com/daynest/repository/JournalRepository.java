package com.daynest.repository;

import com.daynest.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    Optional<JournalEntry> findByEntryDate(LocalDate date);
}
