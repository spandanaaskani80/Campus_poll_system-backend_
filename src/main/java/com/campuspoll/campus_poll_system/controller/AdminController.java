package com.campuspoll.campus_poll_system.controller;

import com.campuspoll.campus_poll_system.dto.*;
import com.campuspoll.campus_poll_system.security.UserDetailsImpl;
import com.campuspoll.campus_poll_system.service.AdminService;
import com.campuspoll.campus_poll_system.service.PollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final PollService pollService;

    public AdminController(AdminService adminService, PollService pollService) {
        this.adminService = adminService;
        this.pollService = pollService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/polls")
    public ResponseEntity<List<PollResponse>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPollsAdmin());
    }

    @PostMapping("/polls")
    public ResponseEntity<PollResponse> createPoll(@Valid @RequestBody PollRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long adminId = currentUser != null ? currentUser.getId() : null;
        PollResponse response = pollService.createPoll(request, adminId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/polls/{pollId}")
    public ResponseEntity<PollResponse> getPollById(@PathVariable Long pollId,
                                                    @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(pollService.getPollById(pollId, currentUserId));
    }

    @PutMapping("/polls/{pollId}")
    public ResponseEntity<PollResponse> updatePoll(@PathVariable Long pollId,
                                                   @Valid @RequestBody PollRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long adminId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(pollService.updatePoll(pollId, request, adminId));
    }

    @PostMapping("/polls/{pollId}/publish")
    public ResponseEntity<PollResponse> publishPoll(@PathVariable Long pollId) {
        return ResponseEntity.ok(pollService.publishPoll(pollId));
    }

    @PostMapping("/polls/{pollId}/close")
    public ResponseEntity<PollResponse> closePoll(@PathVariable Long pollId) {
        return ResponseEntity.ok(pollService.closePoll(pollId));
    }

    @GetMapping("/polls/{pollId}/results")
    public ResponseEntity<PollResultResponse> getPollResults(@PathVariable Long pollId,
                                                             @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(pollService.calculatePollResults(pollId, currentUser));
    }
}
