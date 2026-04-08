package com.klu.sdp36BE.repository;

import com.klu.sdp36BE.model.UserResourceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import com.klu.sdp36BE.model.User;
import com.klu.sdp36BE.model.Resource;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

public interface UserResourceHistoryRepository extends JpaRepository<UserResourceHistory, Long> {
    List<UserResourceHistory> findByUser(User user);
    long countByResource(Resource resource);

    @Query("SELECT h.resource.title, COUNT(h) as cnt FROM UserResourceHistory h GROUP BY h.resource.id ORDER BY cnt DESC")
    List<Object[]> findTopResources(Pageable pageable);

    @Query("SELECT h.user.name, h.resource.title, h.accessedAt FROM UserResourceHistory h ORDER BY h.accessedAt DESC")
    List<Object[]> findRecentActivity(Pageable pageable);
}
