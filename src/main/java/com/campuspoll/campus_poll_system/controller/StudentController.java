package com.campuspoll.campus_poll_system.controller;

import com.campuspoll.campus_poll_system.dto.*;
import com.campuspoll.campus_poll_system.security.UserDetailsImpl;
import com.campuspoll.campus_poll_system.service.PollService;
import com.campuspoll.campus_poll_system.service.StudentService;
import com.campuspoll.campus_poll_system.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final PollService pollService;
    private final VoteService voteService;

    public StudentController(StudentService studentService,
                             PollService pollService,
                             VoteService voteService) {
        this.studentService = studentService;
        this.pollService = pollService;
        this.voteService = voteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardDto> getDashboard(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(studentService.getStudentDashboard(currentUser.getId()));
    }

    @GetMapping("/polls")
    public ResponseEntity<List<PollResponse>> getAllEligiblePolls(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(studentService.getAllPublishedPolls(currentUser.getId()));
    }

    @GetMapping("/polls/active")
    public ResponseEntity<List<PollResponse>> getActivePolls(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(studentService.getActivePolls(currentUser.getId()));
    }

    @GetMapping("/polls/upcoming")
    public ResponseEntity<List<PollResponse>> getUpcomingPolls(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(studentService.getUpcomingPolls(currentUser.getId()));
    }

    @GetMapping("/polls/closed")
    public ResponseEntity<List<PollResponse>> getClosedPolls(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(studentService.getClosedPolls(currentUser.getId()));
    }

    @GetMapping("/polls/{pollId}")
    public ResponseEntity<PollResponse> getPollDetails(@PathVariable Long pollId,
                                                       @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(pollService.getPollById(pollId, currentUser.getId()));
    }

    @PostMapping("/polls/{pollId}/vote")
    public ResponseEntity<VoteResponse> submitVote(@PathVariable Long pollId,
                                                   @Valid @RequestBody VoteRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl currentUser) {
        VoteResponse response = voteService.submitVote(pollId, request.getOptionId(), currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/polls/{pollId}/results")
    public ResponseEntity<PollResultResponse> getPollResults(@PathVariable Long pollId,
                                                             @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(pollService.calculatePollResults(pollId, currentUser));
    }

    @GetMapping("/voting-history")
    public ResponseEntity<List<VoteResponse>> getVotingHistory(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(voteService.getVotingHistory(currentUser.getId()));
    }
}
