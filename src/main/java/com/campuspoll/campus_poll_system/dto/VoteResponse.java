package com.campuspoll.campus_poll_system.dto;

import java.time.LocalDateTime;

public class VoteResponse {
    private boolean success;
    private String message;
    private Long voteId;
    private Long pollId;
    private Long optionId;
    private LocalDateTime votedAt;

    public VoteResponse() {
    }

    public VoteResponse(boolean success, String message, Long voteId, Long pollId, Long optionId, LocalDateTime votedAt) {
        this.success = success;
        this.message = message;
        this.voteId = voteId;
        this.pollId = pollId;
        this.optionId = optionId;
        this.votedAt = votedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
}
