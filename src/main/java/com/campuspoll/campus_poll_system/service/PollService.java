package com.campuspoll.campus_poll_system.service;

import com.campuspoll.campus_poll_system.dto.*;
import com.campuspoll.campus_poll_system.entity.Poll;
import com.campuspoll.campus_poll_system.entity.PollOption;
import com.campuspoll.campus_poll_system.exception.BadRequestException;
import com.campuspoll.campus_poll_system.exception.ResourceNotFoundException;
import com.campuspoll.campus_poll_system.repository.PollOptionRepository;
import com.campuspoll.campus_poll_system.repository.PollRepository;
import com.campuspoll.campus_poll_system.repository.VoteRepository;
import com.campuspoll.campus_poll_system.security.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final VoteRepository voteRepository;

    public PollService(PollRepository pollRepository,
                       PollOptionRepository pollOptionRepository,
                       VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public PollResponse createPoll(PollRequest request, Long adminId) {
        validatePollDates(request.getStartTime(), request.getEndTime());
        validateOptions(request.getOptions());

        Poll poll = new Poll();
        poll.setTitle(request.getTitle());
        poll.setQuestion(request.getQuestion());
        poll.setDescription(request.getDescription());
        poll.setStartTime(request.getStartTime());
        poll.setEndTime(request.getEndTime());
        poll.setPublished(request.isPublished());
        poll.setResultVisibility(request.getResultVisibility());
        poll.setCreatedBy(adminId);

        LocalDateTime now = LocalDateTime.now();
        if (request.isPublished()) {
            if (now.isBefore(request.getStartTime())) {
                poll.setStatus("SCHEDULED");
            } else if (now.isAfter(request.getEndTime())) {
                poll.setStatus("CLOSED");
            } else {
                poll.setStatus("ACTIVE");
            }
        } else {
            poll.setStatus("DRAFT");
        }

        for (String optText : request.getOptions()) {
            if (optText != null && !optText.trim().isEmpty()) {
                poll.addOption(new PollOption(optText.trim()));
            }
        }

        Poll saved = pollRepository.save(poll);
        return toPollResponse(saved, null);
    }

    @Transactional
    public PollResponse updatePoll(Long pollId, PollRequest request, Long adminId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));

        validatePollDates(request.getStartTime(), request.getEndTime());

        long voteCount = voteRepository.countByPollId(pollId);
        if (voteCount > 0) {
            // If voting has already started, preserve questions and options for integrity
            poll.setTitle(request.getTitle());
            poll.setDescription(request.getDescription());
            poll.setEndTime(request.getEndTime());
            poll.setResultVisibility(request.getResultVisibility());
        } else {
            validateOptions(request.getOptions());
            poll.setTitle(request.getTitle());
            poll.setQuestion(request.getQuestion());
            poll.setDescription(request.getDescription());
            poll.setStartTime(request.getStartTime());
            poll.setEndTime(request.getEndTime());
            poll.setPublished(request.isPublished());
            poll.setResultVisibility(request.getResultVisibility());

            poll.getOptions().clear();
            for (String optText : request.getOptions()) {
                if (optText != null && !optText.trim().isEmpty()) {
                    poll.addOption(new PollOption(optText.trim()));
                }
            }
        }

        updatePollStatus(poll);
        Poll updated = pollRepository.save(poll);
        return toPollResponse(updated, null);
    }

    @Transactional
    public PollResponse publishPoll(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));

        poll.setPublished(true);
        updatePollStatus(poll);
        Poll saved = pollRepository.save(poll);
        return toPollResponse(saved, null);
    }

    @Transactional
    public PollResponse closePoll(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));

        poll.setStatus("CLOSED");
        if (poll.getEndTime().isAfter(LocalDateTime.now())) {
            poll.setEndTime(LocalDateTime.now());
        }
        Poll saved = pollRepository.save(poll);
        return toPollResponse(saved, null);
    }

    @Transactional(readOnly = true)
    public List<PollResponse> getAllPollsAdmin() {
        return pollRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(p -> toPollResponse(p, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PollResponse getPollById(Long pollId, Long currentUserId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));
        return toPollResponse(poll, currentUserId);
    }

    @Transactional(readOnly = true)
    public PollResultResponse calculatePollResults(Long pollId, UserDetailsImpl currentUser) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isClosed = isPollClosed(poll);

        if (!isAdmin) {
            String visibility = poll.getResultVisibility();
            if ("ADMIN_ONLY".equalsIgnoreCase(visibility)) {
                throw new AccessDeniedException("Results for this poll are restricted to administrators.");
            }
            if ("AFTER_CLOSE".equalsIgnoreCase(visibility) && !isClosed) {
                throw new BadRequestException("Results will become visible after the poll is closed.");
            }
        }

        long totalVotes = voteRepository.countByPollId(pollId);
        List<PollOption> options = pollOptionRepository.findByPollId(pollId);

        List<OptionResultDto> optionResults = new ArrayList<>();
        String highestVotedOption = "None";
        long maxVotes = -1;

        for (PollOption opt : options) {
            long count = voteRepository.countByOptionId(opt.getId());
            double percentage = totalVotes > 0 ? Math.round(((double) count / totalVotes * 100.0) * 10.0) / 10.0 : 0.0;
            optionResults.add(new OptionResultDto(opt.getId(), opt.getOptionText(), count, percentage));

            if (count > maxVotes && count > 0) {
                maxVotes = count;
                highestVotedOption = opt.getOptionText();
            }
        }

        PollResultResponse response = new PollResultResponse();
        response.setPollId(poll.getId());
        response.setPollTitle(poll.getTitle());
        response.setQuestion(poll.getQuestion());
        response.setDescription(poll.getDescription());
        response.setTotalVotes(totalVotes);
        response.setOptions(optionResults);
        response.setHighestVotedOption(highestVotedOption);
        response.setResultVisibility(poll.getResultVisibility());
        response.setStatus(poll.getStatus());
        response.setClosed(isClosed);

        return response;
    }

    public PollResponse toPollResponse(Poll poll, Long currentUserId) {
        PollResponse res = new PollResponse();
        res.setId(poll.getId());
        res.setTitle(poll.getTitle());
        res.setQuestion(poll.getQuestion());
        res.setDescription(poll.getDescription());
        res.setStartTime(poll.getStartTime());
        res.setEndTime(poll.getEndTime());
        res.setPublished(poll.isPublished());
        res.setResultVisibility(poll.getResultVisibility());
        res.setStatus(resolveStatus(poll));
        res.setCreatedBy(poll.getCreatedBy());
        res.setCreatedAt(poll.getCreatedAt());

        long totalVotes = voteRepository.countByPollId(poll.getId());
        res.setTotalVotes(totalVotes);

        List<PollOptionDto> optionDtos = poll.getOptions().stream()
                .map(opt -> new PollOptionDto(opt.getId(), opt.getOptionText()))
                .collect(Collectors.toList());
        res.setOptions(optionDtos);

        if (currentUserId != null) {
            voteRepository.findByStudentIdAndPollId(currentUserId, poll.getId())
                    .ifPresent(v -> {
                        res.setHasVoted(true);
                        res.setUserVotedOptionId(v.getOption().getId());
                    });
        }

        boolean isClosed = isPollClosed(poll);
        boolean canView = "ALWAYS".equalsIgnoreCase(poll.getResultVisibility())
                || ("AFTER_CLOSE".equalsIgnoreCase(poll.getResultVisibility()) && isClosed);
        res.setCanViewResults(canView);
        res.setActive(!isClosed && poll.isPublished() && LocalDateTime.now().isAfter(poll.getStartTime()));

        return res;
    }

    public boolean isPollClosed(Poll poll) {
        return "CLOSED".equalsIgnoreCase(poll.getStatus()) || LocalDateTime.now().isAfter(poll.getEndTime());
    }

    private void updatePollStatus(Poll poll) {
        if (!poll.isPublished()) {
            poll.setStatus("DRAFT");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if ("CLOSED".equalsIgnoreCase(poll.getStatus()) || now.isAfter(poll.getEndTime())) {
            poll.setStatus("CLOSED");
        } else if (now.isBefore(poll.getStartTime())) {
            poll.setStatus("SCHEDULED");
        } else {
            poll.setStatus("ACTIVE");
        }
    }

    private String resolveStatus(Poll poll) {
        if (!poll.isPublished()) {
            return "DRAFT";
        }
        LocalDateTime now = LocalDateTime.now();
        if ("CLOSED".equalsIgnoreCase(poll.getStatus()) || now.isAfter(poll.getEndTime())) {
            return "CLOSED";
        } else if (now.isBefore(poll.getStartTime())) {
            return "SCHEDULED";
        } else {
            return "ACTIVE";
        }
    }

    private void validatePollDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("Start and End dates are required.");
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException("End date and time must be strictly after Start date and time.");
        }
    }

    private void validateOptions(List<String> options) {
        if (options == null || options.stream().filter(s -> s != null && !s.trim().isEmpty()).count() < 2) {
            throw new BadRequestException("A poll must contain at least two valid non-empty answer options.");
        }
    }
}
