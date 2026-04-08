package com.klu.sdp36BE.controller;

import com.klu.sdp36BE.model.*;
import com.klu.sdp36BE.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserEnrollmentRepository enrollmentRepository;

    @Autowired
    private UserResourceHistoryRepository historyRepository;

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        return ResponseEntity.ok(getLoggedInUser());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        User user = getLoggedInUser();

        List<UserEnrollment> enrollments = enrollmentRepository.findByUser(user);
        List<UserResourceHistory> history = historyRepository.findByUser(user);

        Map<String, Object> data = new HashMap<>();
        
        data.put("enrolledPrograms", enrollments.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getProgram().getId());
            m.put("title", e.getProgram().getTitle());
            m.put("instructor", e.getProgram().getInstructorName());
            m.put("duration", e.getProgram().getDuration());
            m.put("enrolledAt", e.getEnrolledAt());
            return m;
        }).collect(Collectors.toList()));

        data.put("resourceHistory", history.stream().map(h -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", h.getResource().getId());
            m.put("title", h.getResource().getTitle());
            m.put("category", h.getResource().getCategory());
            m.put("author", h.getResource().getAuthor());
            m.put("accessedAt", h.getAccessedAt());
            return m;
        }).collect(Collectors.toList()));

        data.put("totalEnrolled", enrollments.size());
        data.put("totalResourcesAccessed", history.size());

        return ResponseEntity.ok(data);
    }

    @PostMapping("/enroll/{programId}")
    public ResponseEntity<?> enroll(@PathVariable Long programId) {
        User user = getLoggedInUser();
        Program program = programRepository.findById(programId).orElseThrow(() -> new RuntimeException("Program not found"));

        if (enrollmentRepository.findByUserAndProgram(user, program).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Already enrolled"));
        }

        UserEnrollment enrollment = new UserEnrollment();
        enrollment.setUser(user);
        enrollment.setProgram(program);
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(Map.of("message", "Successfully enrolled"));
    }

    @DeleteMapping("/unenroll/{programId}")
    public ResponseEntity<?> unenroll(@PathVariable Long programId) {
        User user = getLoggedInUser();
        Program program = programRepository.findById(programId).orElseThrow(() -> new RuntimeException("Program not found"));

        UserEnrollment enrollment = enrollmentRepository.findByUserAndProgram(user, program)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        return ResponseEntity.ok(Map.of("message", "Successfully unenrolled"));
    }

    @PostMapping("/resource-access/{resourceId}")
    public ResponseEntity<?> logResourceAccess(@PathVariable Long resourceId) {
        User user = getLoggedInUser();
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new RuntimeException("Resource not found"));

        UserResourceHistory log = new UserResourceHistory();
        log.setUser(user);
        log.setResource(resource);
        historyRepository.save(log);

        return ResponseEntity.ok(Map.of("message", "Access logged"));
    }
}
