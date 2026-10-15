package com.campuspoll.campus_poll_system.service;

import com.campuspoll.campus_poll_system.dto.PollResponse;
import com.campuspoll.campus_poll_system.dto.StudentDashboardDto;
import com.campuspoll.campus_poll_system.dto.VoteResponse;
import com.campuspoll.campus_poll_system.entity.User;
import com.campuspoll.campus_poll_system.exception.ResourceNotFoundException;
import com.campuspoll.campus_poll_system.repository.PollRepository;
import com.campuspoll.campus_poll_system.repository.UserRepository;
import com.campuspoll.campus_poll_system.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final PollService pollService;
    private final VoteService voteService;

    public StudentService(UserRepository userRepository,
                          PollRepository pollRepository,
                          VoteRepository voteRepository,
                          PollService pollService,
                          VoteService voteService) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.pollService = pollService;
        this.voteService = voteService;
    }

    @Transactional(readOnly = true)
    public StudentDashboardDto getStudentDashboard(Long studentUserId) {
        User student = userRepository.findById(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + studentUserId));

        LocalDateTime now = LocalDateTime.now();
        long activeCount = pollRepository.countActivePolls(now);
        long upcomingCount = pollRepository.countUpcomingPolls(now);
        long closedCount = pollRepository.countClosedPolls(now);
        long votedCount = voteRepository.findVotedPollIdsByStudentId(studentUserId).size();

        List<PollResponse> activePolls = pollRepository.findActivePolls(now).stream()
                .map(p -> pollService.toPollResponse(p, studentUserId))
                .collect(Collectors.toList());

        List<VoteResponse> recentVotes = voteService.getVotingHistory(studentUserId);

        StudentDashboardDto dto = new StudentDashboardDto();
        dto.setStudentName(student.getName());
        dto.setStudentId(student.getStudentId());
        dto.setEmail(student.getEmail());
        dto.setActivePollsCount(activeCount);
        dto.setUpcomingPollsCount(upcomingCount);
        dto.setClosedPollsCount(closedCount);
        dto.setVotedPollsCount(votedCount);
        dto.setActivePolls(activePolls);
        dto.setRecentVotes(recentVotes);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<PollResponse> getActivePolls(Long studentUserId) {
        return pollRepository.findActivePolls(LocalDateTime.now()).stream()
                .map(p -> pollService.toPollResponse(p, studentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PollResponse> getUpcomingPolls(Long studentUserId) {
        return pollRepository.findUpcomingPolls(LocalDateTime.now()).stream()
                .map(p -> pollService.toPollResponse(p, studentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PollResponse> getClosedPolls(Long studentUserId) {
        return pollRepository.findClosedPolls(LocalDateTime.now()).stream()
                .map(p -> pollService.toPollResponse(p, studentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PollResponse> getAllPublishedPolls(Long studentUserId) {
        return pollRepository.findByPublishedTrueOrderByCreatedAtDesc().stream()
                .map(p -> pollService.toPollResponse(p, studentUserId))
                .collect(Collectors.toList());
    }
}
