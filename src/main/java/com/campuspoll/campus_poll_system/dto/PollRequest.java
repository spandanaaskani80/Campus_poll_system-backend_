package com.campuspoll.campus_poll_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class PollRequest {

    @NotBlank(message = "Poll title is required")
    private String title;

    @NotBlank(message = "Poll question is required")
    private String question;

    private String description;

    @NotNull(message = "Start date & time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End date & time is required")
    private LocalDateTime endTime;

    private boolean published = false;

    private String resultVisibility = "ALWAYS"; // ALWAYS, AFTER_CLOSE, ADMIN_ONLY

    private String status = "DRAFT"; // DRAFT, SCHEDULED, ACTIVE, CLOSED

    @NotEmpty(message = "At least two options are required")
    private List<String> options;

    public PollRequest() {
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
        return resultVisibility != null ? resultVisibility : "ALWAYS";
    }

    public void setResultVisibility(String resultVisibility) {
        this.resultVisibility = resultVisibility;
    }

    public String getStatus() {
        return status != null ? status : "DRAFT";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
