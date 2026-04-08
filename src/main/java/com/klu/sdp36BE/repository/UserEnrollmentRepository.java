package com.klu.sdp36BE.repository;

import com.klu.sdp36BE.model.Program;
import com.klu.sdp36BE.model.User;
import com.klu.sdp36BE.model.UserEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserEnrollmentRepository extends JpaRepository<UserEnrollment, Long> {
    List<UserEnrollment> findByUser(User user);
    Optional<UserEnrollment> findByUserAndProgram(User user, Program program);
    long countByProgram(Program program);
}
