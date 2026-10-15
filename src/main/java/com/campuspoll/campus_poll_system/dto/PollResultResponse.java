package com.campuspoll.campus_poll_system.dto;

import java.util.List;

public class PollResultResponse {
    private Long pollId;
    private String pollTitle;
    private String question;
    private String description;
    private long totalVotes;
    private List<OptionResultDto> options;
    private String highestVotedOption;
    private String resultVisibility;
    private String status;
    private boolean closed;

    public PollResultResponse() {
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
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

    public long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<OptionResultDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResultDto> options) {
        this.options = options;
    }

    public String getHighestVotedOption() {
        return highestVotedOption;
    }

    public void setHighestVotedOption(String highestVotedOption) {
        this.highestVotedOption = highestVotedOption;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
