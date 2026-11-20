package com.daynest.repository;

import com.daynest.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
    List<Note> findTop5ByOrderByUpdatedAtDesc();
}
