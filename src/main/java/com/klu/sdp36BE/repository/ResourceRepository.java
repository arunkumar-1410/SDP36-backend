package com.klu.sdp36BE.repository;

import com.klu.sdp36BE.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    boolean existsByTitle(String title);
}