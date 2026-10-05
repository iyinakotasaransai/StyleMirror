package com.stylemirror.repository;

import com.stylemirror.entity.AISuggestionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AISuggestionLogRepository extends JpaRepository<AISuggestionLog, Long> {
    List<AISuggestionLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<AISuggestionLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<AISuggestionLog> findByProductIdOrderByCreatedAtDesc(Long productId);
    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);
}
