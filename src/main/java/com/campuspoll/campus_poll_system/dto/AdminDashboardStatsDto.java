package com.campuspoll.campus_poll_system.dto;

public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalStudents;
    private long totalPolls;
    private long activePolls;
    private long scheduledPolls;
    private long closedPolls;
    private long totalValidVotes;

    public AdminDashboardStatsDto() {
    }

    public AdminDashboardStatsDto(long totalUsers, long totalStudents, long totalPolls, long activePolls, long scheduledPolls, long closedPolls, long totalValidVotes) {
        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.totalPolls = totalPolls;
        this.activePolls = activePolls;
        this.scheduledPolls = scheduledPolls;
        this.closedPolls = closedPolls;
        this.totalValidVotes = totalValidVotes;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalPolls() {
        return totalPolls;
    }

    public void setTotalPolls(long totalPolls) {
        this.totalPolls = totalPolls;
    }

    public long getActivePolls() {
        return activePolls;
    }

    public void setActivePolls(long activePolls) {
        this.activePolls = activePolls;
    }

    public long getScheduledPolls() {
        return scheduledPolls;
    }

    public void setScheduledPolls(long scheduledPolls) {
        this.scheduledPolls = scheduledPolls;
    }

    public long getClosedPolls() {
        return closedPolls;
    }

    public void setClosedPolls(long closedPolls) {
        this.closedPolls = closedPolls;
    }

    public long getTotalValidVotes() {
        return totalValidVotes;
    }

    public void setTotalValidVotes(long totalValidVotes) {
        this.totalValidVotes = totalValidVotes;
    }
}
