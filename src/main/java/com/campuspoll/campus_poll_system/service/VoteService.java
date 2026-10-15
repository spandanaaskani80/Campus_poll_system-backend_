package com.campuspoll.campus_poll_system.service;

import com.campuspoll.campus_poll_system.dto.VoteResponse;
import com.campuspoll.campus_poll_system.entity.Poll;
import com.campuspoll.campus_poll_system.entity.PollOption;
import com.campuspoll.campus_poll_system.entity.User;
import com.campuspoll.campus_poll_system.entity.Vote;
import com.campuspoll.campus_poll_system.exception.BadRequestException;
import com.campuspoll.campus_poll_system.exception.ConflictException;
import com.campuspoll.campus_poll_system.exception.ResourceNotFoundException;
import com.campuspoll.campus_poll_system.repository.PollOptionRepository;
import com.campuspoll.campus_poll_system.repository.PollRepository;
import com.campuspoll.campus_poll_system.repository.UserRepository;
import com.campuspoll.campus_poll_system.repository.VoteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final UserRepository userRepository;

    public VoteService(VoteRepository voteRepository,
                       PollRepository pollRepository,
                       PollOptionRepository pollOptionRepository,
                       UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public VoteResponse submitVote(Long pollId, Long optionId, Long studentUserId) {
        User student = userRepository.findById(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + studentUserId));

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));

        if (!poll.isPublished()) {
            throw new BadRequestException("Poll is not published yet.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(poll.getStartTime())) {
            throw new BadRequestException("Voting has not started yet. This poll opens at " + poll.getStartTime());
        }

        if (now.isAfter(poll.getEndTime()) || "CLOSED".equalsIgnoreCase(poll.getStatus())) {
            throw new BadRequestException("Poll is closed and is no longer accepting votes.");
        }

        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));

        if (!option.getPoll().getId().equals(poll.getId())) {
            throw new BadRequestException("The selected option does not belong to the requested poll.");
        }

        // Database and service duplicate prevention
        if (voteRepository.existsByStudentIdAndPollId(studentUserId, pollId)) {
            throw new ConflictException("You have already voted in this poll.");
        }

        try {
            Vote vote = new Vote(student, poll, option);
            Vote saved = voteRepository.save(vote);
            return new VoteResponse(true, "Vote submitted successfully", saved.getId(), poll.getId(), option.getId(), LocalDateTime.now());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("You have already voted in this poll.");
        }
    }

    @Transactional(readOnly = true)
    public List<VoteResponse> getVotingHistory(Long studentUserId) {
        return voteRepository.findVotingHistoryByStudentId(studentUserId).stream()
                .map(v -> new VoteResponse(true, "Voted in: " + v.getPoll().getTitle(), v.getId(), v.getPoll().getId(), v.getOption().getId(), v.getVotedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasStudentVoted(Long studentUserId, Long pollId) {
        return voteRepository.existsByStudentIdAndPollId(studentUserId, pollId);
    }
}
