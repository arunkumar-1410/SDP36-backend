package com.klu.sdp36BE.service;

import com.klu.sdp36BE.model.User;
import com.klu.sdp36BE.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        // 1. Check if email exists
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // 2. Ensure role is student if not set
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("student");
        }
        
        // 3. Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        try {
            // 4. Save with all fields set
            return repo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation: Email might already exist");
        }
    }

    public User login(String email, String rawPassword) {
        // 1. Find user by email
        Optional<User> u = repo.findByEmail(email);

        // 2. Compare encoded passwords
        if (u.isPresent() && passwordEncoder.matches(rawPassword, u.get().getPassword())) {
            return u.get();
        }
        
        throw new RuntimeException("Invalid credentials");
    }
}