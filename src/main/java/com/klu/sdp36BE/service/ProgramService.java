package com.klu.sdp36BE.service;

import com.klu.sdp36BE.model.Program;
import com.klu.sdp36BE.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramService {

    private final ProgramRepository repo;

    public ProgramService(ProgramRepository repo) {
        this.repo = repo;
    }

    public List<Program> getAllPrograms() {
        return repo.findAll();
    }

    public Program addProgram(Program p) {
        return repo.save(p);
    }
}