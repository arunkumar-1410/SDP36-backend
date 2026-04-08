package com.klu.sdp36BE.repository;

import com.klu.sdp36BE.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findAllByTitle(String title);
    boolean existsByTitle(String title);
}