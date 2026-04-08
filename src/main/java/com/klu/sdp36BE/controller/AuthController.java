package com.klu.sdp36BE.controller;

import com.klu.sdp36BE.model.PasswordResetToken;
import com.klu.sdp36BE.model.User;
import com.klu.sdp36BE.repository.PasswordResetTokenRepository;
import com.klu.sdp36BE.repository.UserRepository;
import com.klu.sdp36BE.service.AuthService;
import com.klu.sdp36BE.service.EmailService;
import com.klu.sdp36BE.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // Updated for current dev port
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            service.register(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            User user = service.login(email, password);
            String token = jwtUtil.generateToken(user.getEmail());
            
            String roleStr = user.getRole(); // Already "ADMIN" or "STUDENT" string
            System.out.println("LOGIN RESPONSE ROLE: " + roleStr);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", roleStr);
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user, 15);
            tokenRepository.findByUser(user).ifPresent(t -> tokenRepository.delete(t));
            tokenRepository.save(resetToken);
            emailService.sendResetEmail(email, token);
        }

        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isPresent() && !tokenOpt.get().isExpired()) {
            User user = tokenOpt.get().getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenRepository.delete(tokenOpt.get());
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        }

        return ResponseEntity.status(400).body(Map.of("message", "Invalid or expired token"));
    }
}