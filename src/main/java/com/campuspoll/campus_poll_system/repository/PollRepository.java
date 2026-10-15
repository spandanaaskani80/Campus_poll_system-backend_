package com.campuspoll.campus_poll_system.repository;

import com.campuspoll.campus_poll_system.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    List<Poll> findAllByOrderByCreatedAtDesc();

    List<Poll> findByPublishedTrueOrderByCreatedAtDesc();

    @Query("SELECT p FROM Poll p WHERE p.published = true AND p.startTime <= :now AND p.endTime >= :now AND p.status <> 'CLOSED' ORDER BY p.endTime ASC")
    List<Poll> findActivePolls(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Poll p WHERE p.published = true AND (p.startTime > :now OR p.status = 'SCHEDULED') ORDER BY p.startTime ASC")
    List<Poll> findUpcomingPolls(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Poll p WHERE p.published = true AND (p.endTime < :now OR p.status = 'CLOSED') ORDER BY p.endTime DESC")
    List<Poll> findClosedPolls(@Param("now") LocalDateTime now);

    long countByPublishedTrue();

    @Query("SELECT COUNT(p) FROM Poll p WHERE p.published = true AND p.startTime <= :now AND p.endTime >= :now AND p.status <> 'CLOSED'")
    long countActivePolls(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM Poll p WHERE p.published = true AND (p.startTime > :now OR p.status = 'SCHEDULED')")
    long countUpcomingPolls(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM Poll p WHERE p.endTime < :now OR p.status = 'CLOSED'")
    long countClosedPolls(@Param("now") LocalDateTime now);
}
