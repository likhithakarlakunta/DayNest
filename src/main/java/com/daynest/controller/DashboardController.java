package com.daynest.controller;

import com.daynest.model.Task;
import com.daynest.repository.NoteRepository;
import com.daynest.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final TaskRepository tasks;
    private final NoteRepository notes;
    public DashboardController(TaskRepository tasks, NoteRepository notes) { this.tasks = tasks; this.notes = notes; }

    @GetMapping
    public Map<String, Object> summary() {
        LocalDate today = LocalDate.now();
        long total = tasks.count();
        long done = tasks.countByStatus(Task.Status.COMPLETED);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("todayTasks", tasks.filter(null, null, null, today));
        m.put("upcoming", tasks.findByDueDateBetweenAndStatusNot(today.plusDays(1), today.plusDays(7), Task.Status.COMPLETED));
        m.put("overdueCount", tasks.findByDueDateBeforeAndStatusNot(today, Task.Status.COMPLETED).size());
        m.put("totalTasks", total);
        m.put("completedTasks", done);
        m.put("progressPercent", total == 0 ? 0 : Math.round(done * 100.0 / total));
        m.put("recentNotes", notes.findTop5ByOrderByUpdatedAtDesc());
        return m;
    }
}
