package com.klu.sdp36BE.service;

import com.klu.sdp36BE.model.Resource;
import com.klu.sdp36BE.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository repo;

    public ResourceService(ResourceRepository repo) {
        this.repo = repo;
    }

    public List<Resource> getAllResources() {
        return repo.findAll();
    }

    public Resource addResource(Resource r) {
        return repo.save(r);
    }

    public void deleteResource(Long id) {
        repo.deleteById(id);
    }
}