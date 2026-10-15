package com.campuspoll.campus_poll_system.service;

import com.campuspoll.campus_poll_system.dto.AdminDashboardStatsDto;
import com.campuspoll.campus_poll_system.dto.UserDto;
import com.campuspoll.campus_poll_system.repository.PollRepository;
import com.campuspoll.campus_poll_system.repository.UserRepository;
import com.campuspoll.campus_poll_system.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public AdminService(UserRepository userRepository,
                        PollRepository pollRepository,
                        VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardStatsDto getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole("STUDENT");
        long totalPolls = pollRepository.count();
        long activePolls = pollRepository.countActivePolls(now);
        long scheduledPolls = pollRepository.countUpcomingPolls(now);
        long closedPolls = pollRepository.countClosedPolls(now);
        long totalValidVotes = voteRepository.count();

        return new AdminDashboardStatsDto(
                totalUsers,
                totalStudents,
                totalPolls,
                activePolls,
                scheduledPolls,
                closedPolls,
                totalValidVotes
        );
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(u -> new UserDto(
                        u.getId(),
                        u.getName(),
                        u.getStudentId(),
                        u.getEmail(),
                        u.getRole(),
                        u.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
