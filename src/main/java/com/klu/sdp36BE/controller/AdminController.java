package com.klu.sdp36BE.controller;

import com.klu.sdp36BE.model.*;
import com.klu.sdp36BE.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private UserEnrollmentRepository enrollmentRepository;

    @Autowired
    private UserResourceHistoryRepository historyRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        System.out.println("DEBUG: AUTH CONTEXT: " + SecurityContextHolder.getContext().getAuthentication());
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userRepository.count());
            stats.put("totalResources", resourceRepository.count());
            stats.put("totalPrograms", programRepository.count());
            stats.put("totalEnrollments", enrollmentRepository.count());
            stats.put("totalBookReads", historyRepository.count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("totalUsers", 0, "totalResources", 0, "totalPrograms", 0, "totalBookReads", 0, "totalEnrollments", 0));
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<?> getRecentActivity() {
        try {
            List<Object[]> results = historyRepository.findRecentActivity(PageRequest.of(0, 10));
            return ResponseEntity.ok(results.stream().map(res -> {
                Map<String, Object> m = new HashMap<>();
                m.put("userName", res[0]);
                m.put("resourceTitle", res[1]);
                m.put("accessedAt", res[2]);
                return m;
            }).collect(Collectors.toList()));
        } catch (Exception e) { return ResponseEntity.ok(Collections.emptyList()); }
    }

    @GetMapping("/top-resources")
    public ResponseEntity<?> getTopResources() {
        try {
            List<Object[]> results = historyRepository.findTopResources(PageRequest.of(0, 5));
            return ResponseEntity.ok(results.stream().map(res -> {
                Map<String, Object> m = new HashMap<>();
                m.put("resourceTitle", res[0]);
                m.put("readCount", res[1]);
                return m;
            }).collect(Collectors.toList()));
        } catch (Exception e) { return ResponseEntity.ok(Collections.emptyList()); }
    }

    // --- RESOURCES CRUD ---
    @GetMapping("/resources")
    public ResponseEntity<?> getAllResources() {
        return ResponseEntity.ok(resourceRepository.findAll());
    }

    @PostMapping("/resources")
    public ResponseEntity<?> addResource(@RequestBody Resource r) {
        return ResponseEntity.ok(resourceRepository.save(r));
    }

    @PutMapping("/resources/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody Resource r) {
        r.setId(id);
        return ResponseEntity.ok(resourceRepository.save(r));
    }

    @DeleteMapping("/resources/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    // --- PROGRAMS CRUD ---
    @GetMapping("/programs")
    public ResponseEntity<?> getAllPrograms() {
        return ResponseEntity.ok(programRepository.findAll());
    }

    @PostMapping("/programs")
    public ResponseEntity<?> addProgram(@RequestBody Program p) {
        return ResponseEntity.ok(programRepository.save(p));
    }

    @PutMapping("/programs/{id}")
    public ResponseEntity<?> updateProgram(@PathVariable Long id, @RequestBody Program p) {
        p.setId(id);
        return ResponseEntity.ok(programRepository.save(p));
    }

    @DeleteMapping("/programs/{id}")
    public ResponseEntity<?> deleteProgram(@PathVariable Long id) {
        programRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    // --- USERS CRUD ---
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && "ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete ADMIN user"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // --- ACTIVITY ENDPOINTS (for AdminActivity.jsx) ---
    @GetMapping("/enrollments")
    public ResponseEntity<?> getEnrollments() {
        try {
            List<UserEnrollment> enrollments = enrollmentRepository.findAll();
            return ResponseEntity.ok(enrollments.stream().map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("userName", e.getUser().getName());
                m.put("userEmail", e.getUser().getEmail());
                m.put("programTitle", e.getProgram().getTitle());
                m.put("enrolledDate", e.getEnrolledAt());
                return m;
            }).collect(Collectors.toList()));
        } catch (Exception e) { return ResponseEntity.ok(Collections.emptyList()); }
    }

    @GetMapping("/book-reads")
    public ResponseEntity<?> getBookReads() {
        try {
            List<UserResourceHistory> history = historyRepository.findAll(Sort.by(Sort.Direction.DESC, "accessedAt"));
            return ResponseEntity.ok(history.stream().map(h -> {
                Map<String, Object> m = new HashMap<>();
                m.put("userName", h.getUser().getName());
                m.put("userEmail", h.getUser().getEmail());
                m.put("resourceTitle", h.getResource().getTitle());
                m.put("dateAccessed", h.getAccessedAt());
                return m;
            }).collect(Collectors.toList()));
        } catch (Exception e) { return ResponseEntity.ok(Collections.emptyList()); }
    }

    @GetMapping("/enrollments-list")
    public ResponseEntity<?> getEnrollmentsList() {
        try {
            List<UserEnrollment> enrollments = enrollmentRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "enrolledAt"))
            ).getContent();
            return ResponseEntity.ok(enrollments.stream().map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("userName", e.getUser().getName());
                m.put("userEmail", e.getUser().getEmail());
                m.put("programTitle", e.getProgram().getTitle());
                m.put("enrolledAt", e.getEnrolledAt());
                return m;
            }).collect(Collectors.toList()));
        } catch (Exception e) { return ResponseEntity.ok(Collections.emptyList()); }
    }
}
