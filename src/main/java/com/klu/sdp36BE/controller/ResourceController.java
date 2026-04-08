package com.klu.sdp36BE.controller;

import com.klu.sdp36BE.model.*;
import com.klu.sdp36BE.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserResourceHistoryRepository historyRepository;

    @GetMapping
    public List<Resource> getAll() {
        // Returns all fields including pdf_url, cover_image_url, description
        return resourceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getById(@PathVariable Long id) {
        return resourceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/read")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> trackRead(@PathVariable Long id, 
                                        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            if (userDetails == null) return ResponseEntity.ok().build();
            
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            Resource resource = resourceRepository.findById(id).orElse(null);

            if (user != null && resource != null) {
                UserResourceHistory history = new UserResourceHistory();
                history.setUser(user);
                history.setResource(resource);
                history.setAccessedAt(LocalDateTime.now());
                historyRepository.save(history);
                
                System.out.println("📖 Read tracked: " + user.getName() + " read " + resource.getTitle());
                return ResponseEntity.ok(Map.of("message", "Read logged"));
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("❌ Track read error: " + e.getMessage());
            return ResponseEntity.ok().build(); // silent fail as requested
        }
    }
}