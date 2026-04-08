package com.klu.sdp36BE.config;

import com.klu.sdp36BE.model.*;
import com.klu.sdp36BE.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                 ResourceRepository resourceRepository, 
                                 ProgramRepository programRepository,
                                 PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // 1. Admin Verification
                Optional<User> adminOpt = userRepository.findByEmail("admin@healthwell.com");
                if (adminOpt.isEmpty()) {
                    User admin = new User();
                    admin.setName("Admin");
                    admin.setEmail("admin@healthwell.com");
                    admin.setPassword(passwordEncoder.encode("password123"));
                    admin.setRole("ADMIN");
                    userRepository.save(admin);
                    System.out.println("✅ Default admin created: admin@healthwell.com / password123");
                } else {
                    User admin = adminOpt.get();
                    admin.setPassword(passwordEncoder.encode("password123"));
                    userRepository.save(admin);
                    System.out.println("✅ Default admin password reset to password123");
                }

                // 2. Program Seeding (Issue 1)
                // Delete duplicates first, keep only 1 yoga
                List<Program> yogaList = programRepository.findAllByTitle("Yoga Program");
                if (yogaList.size() > 1) {
                    programRepository.deleteAll(yogaList.subList(1, yogaList.size()));
                }

                // Insert Meditation if not exists
                if (!programRepository.existsByTitle("Meditation Classes")) {
                    Program meditation = new Program();
                    meditation.setTitle("Meditation Classes");
                    meditation.setInstructorName("Expert Trainer"); // Mapping to model field
                    meditation.setDescription("Daily mindfulness and meditation sessions for stress relief and mental clarity.");
                    meditation.setDuration("30 DAYS");
                    meditation.setCategory("Wellness");
                    meditation.setImageUrl("https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=500");
                    meditation.setCreatedAt(LocalDateTime.now());
                    programRepository.save(meditation);
                    System.out.println("✅ Inserted: Meditation Classes");
                }

                // Insert HIIT Training if not exists  
                if (!programRepository.existsByTitle("HIIT Training")) {
                    Program hiit = new Program();
                    hiit.setTitle("HIIT Training");
                    hiit.setInstructorName("Fitness Expert"); // Mapping to model field
                    hiit.setDescription("High intensity interval training for maximum fat burn and endurance.");
                    hiit.setDuration("45 DAYS");
                    hiit.setCategory("Fitness");
                    hiit.setImageUrl("https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=500");
                    hiit.setCreatedAt(LocalDateTime.now());
                    programRepository.save(hiit);
                    System.out.println("✅ Inserted: HIIT Training");
                }

                // Insert Nutrition Coaching if not exists
                if (!programRepository.existsByTitle("Nutrition Coaching")) {
                    Program nutrition = new Program();
                    nutrition.setTitle("Nutrition Coaching");
                    nutrition.setInstructorName("Diet Expert"); // Mapping to model field
                    nutrition.setDescription("Personalized nutrition plans and dietary coaching for a healthier lifestyle.");
                    nutrition.setDuration("60 DAYS");
                    nutrition.setCategory("Nutrition");
                    nutrition.setImageUrl("https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=500");
                    nutrition.setCreatedAt(LocalDateTime.now());
                    programRepository.save(nutrition);
                    System.out.println("✅ Inserted: Nutrition Coaching");
                }
            } catch (Exception e) {
                System.err.println("DataLoader Error (Programs): " + e.getMessage());
            }

            try {
                // 3. Resource/Book Seeding
                if (resourceRepository.count() == 0) {
                   seedResources(resourceRepository);
                }

                // Auto-fix existing broken images in DB
                List<Resource> allResources = resourceRepository.findAll();
                boolean updated = false;
                for(Resource r : allResources) {
                    String title = r.getTitle() != null ? r.getTitle() : "";
                    String cover = r.getCoverImageUrl() != null ? r.getCoverImageUrl() : "";
                    
                    if(title.contains("Nutrition") && (cover.isEmpty() || cover.contains("unsplash.com") || cover.contains("drive.google"))) {
                        r.setCoverImageUrl("https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=500&auto=format&fit=crop");
                        updated = true;
                    } else if(title.contains("Fitness") && (cover.isEmpty() || cover.contains("drive.google"))) {
                        r.setCoverImageUrl("https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=500&auto=format&fit=crop");
                        updated = true;
                    } else if(title.contains("Economics") && (cover.isEmpty() || cover.contains("drive.google") || cover.contains("amazon"))) {
                        r.setCoverImageUrl("https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=500&auto=format&fit=crop");
                        updated = true;
                    } else if(title.contains("Development") && (cover.isEmpty() || cover.contains("drive.google"))) {
                        r.setCoverImageUrl("https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=500&auto=format&fit=crop");
                        updated = true;
                    } else if (cover.isEmpty() || cover.contains("drive.google")) {
                        r.setCoverImageUrl("https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=500&auto=format&fit=crop");
                        updated = true;
                    }
                }
                if(updated) {
                    resourceRepository.saveAll(allResources);
                    System.out.println("✅ Fixed broken resource cover images in database!");
                }
            } catch (Exception e) {
                System.err.println("DataLoader Error (Resources): " + e.getMessage());
            }
        };
    }

    private void seedResources(ResourceRepository repo) {
        repo.saveAll(List.of(
            createResource("Health Economics 5th Edition", "Folland, Goodman & Stano", "Health Economics", "A leading textbook covering theory and practice of health economics.", "https://drive.google.com/file/d/1rQZWNkxrvKPLiIk4nLgvWwfXDG2UD8mA/view?usp=drive_link", "https://m.media-amazon.com/images/I/51QnHFHwFAL.jpg"),
             createResource("Diet & Nutrition Guide Vol 1", "Health Experts", "Nutrition", "A complete guide to diet and nutrition for a healthy lifestyle.", "https://drive.google.com/file/d/1ETeFcLkPobWftDEMxv7DQIVl7H56gHf1/view?usp=drive_link", "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=300")
        ));
    }

    private Resource createResource(String title, String author, String cat, String desc, String pdf, String cover) {
        Resource r = new Resource();
        r.setTitle(title);
        r.setAuthor(author);
        r.setCategory(cat);
        r.setDescription(desc);
        r.setPdfUrl(pdf);
        r.setCoverImageUrl(cover);
        r.setCreatedAt(LocalDateTime.now());
        r.setContent("Academic content for " + title);
        return r;
    }
}
