package com.campuspoll.campus_poll_system.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PollResponse {
    private Long id;
    private String title;
    private String question;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean published;
    private String resultVisibility;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private List<PollOptionDto> options;
    private long totalVotes;
    private boolean hasVoted;
    private Long userVotedOptionId;
    private boolean canViewResults;
    private boolean isActive;

    public PollResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getResultVisibility() {
        return resultVisibility;
    }

    public void setResultVisibility(String resultVisibility) {
        this.resultVisibility = resultVisibility;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PollOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<PollOptionDto> options) {
        this.options = options;
    }

    public long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Long getUserVotedOptionId() {
        return userVotedOptionId;
    }

    public void setUserVotedOptionId(Long userVotedOptionId) {
        this.userVotedOptionId = userVotedOptionId;
    }

    public boolean isCanViewResults() {
        return canViewResults;
    }

    public void setCanViewResults(boolean canViewResults) {
        this.canViewResults = canViewResults;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
