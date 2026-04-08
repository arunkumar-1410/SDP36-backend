package com.klu.sdp36BE.controller;

import com.klu.sdp36BE.model.*;
import com.klu.sdp36BE.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/programs")
public class ProgramController {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEnrollmentRepository enrollmentRepository;

    @GetMapping
    public List<Program> getAll() {
        return programRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Program> getById(@PathVariable Long id) {
        return programRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enroll(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        Program program = programRepository.findById(id).orElse(null);

        if (user == null || program == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Authentication required"));
        }

        if (enrollmentRepository.findByUserAndProgram(user, program).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Already enrolled"));
        }

        UserEnrollment enrollment = new UserEnrollment();
        enrollment.setUser(user);
        enrollment.setProgram(program);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(Map.of("message", "Enrolled successfully"));
    }

    @GetMapping("/{id}/enrollment")
    public ResponseEntity<?> checkEnrollment(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(Map.of("isEnrolled", false));

        Program program = programRepository.findById(id).orElse(null);
        if (program == null) return ResponseEntity.notFound().build();

        boolean isEnrolled = enrollmentRepository.findByUserAndProgram(user, program).isPresent();
        return ResponseEntity.ok(Map.of("isEnrolled", isEnrolled));
    }

    @GetMapping("/enrolled")
    public ResponseEntity<?> getEnrolledPrograms() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();
        
        List<UserEnrollment> enrollments = enrollmentRepository.findByUser(user);
        return ResponseEntity.ok(enrollments.stream().map(UserEnrollment::getProgram).toList());
    }
}