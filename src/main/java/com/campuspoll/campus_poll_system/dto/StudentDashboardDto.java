package com.campuspoll.campus_poll_system.dto;

import java.util.List;

public class StudentDashboardDto {
    private String studentName;
    private String studentId;
    private String email;
    private long activePollsCount;
    private long upcomingPollsCount;
    private long closedPollsCount;
    private long votedPollsCount;
    private List<PollResponse> activePolls;
    private List<VoteResponse> recentVotes;

    public StudentDashboardDto() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getActivePollsCount() {
        return activePollsCount;
    }

    public void setActivePollsCount(long activePollsCount) {
        this.activePollsCount = activePollsCount;
    }

    public long getUpcomingPollsCount() {
        return upcomingPollsCount;
    }

    public void setUpcomingPollsCount(long upcomingPollsCount) {
        this.upcomingPollsCount = upcomingPollsCount;
    }

    public long getClosedPollsCount() {
        return closedPollsCount;
    }

    public void setClosedPollsCount(long closedPollsCount) {
        this.closedPollsCount = closedPollsCount;
    }

    public long getVotedPollsCount() {
        return votedPollsCount;
    }

    public void setVotedPollsCount(long votedPollsCount) {
        this.votedPollsCount = votedPollsCount;
    }

    public List<PollResponse> getActivePolls() {
        return activePolls;
    }

    public void setActivePolls(List<PollResponse> activePolls) {
        this.activePolls = activePolls;
    }

    public List<VoteResponse> getRecentVotes() {
        return recentVotes;
    }

    public void setRecentVotes(List<VoteResponse> recentVotes) {
        this.recentVotes = recentVotes;
    }
}
