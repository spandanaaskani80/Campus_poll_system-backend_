package com.campuspoll.campus_poll_system.repository;

import com.campuspoll.campus_poll_system.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByStudentIdAndPollId(Long studentId, Long pollId);

    Optional<Vote> findByStudentIdAndPollId(Long studentId, Long pollId);

    List<Vote> findByPollId(Long pollId);

    long countByPollId(Long pollId);

    long countByOptionId(Long optionId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.poll JOIN FETCH v.option WHERE v.student.id = :studentId ORDER BY v.votedAt DESC")
    List<Vote> findVotingHistoryByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT v.poll.id FROM Vote v WHERE v.student.id = :studentId")
    List<Long> findVotedPollIdsByStudentId(@Param("studentId") Long studentId);
}
