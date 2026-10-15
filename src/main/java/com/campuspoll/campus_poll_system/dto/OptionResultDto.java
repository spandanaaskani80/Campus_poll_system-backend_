package com.campuspoll.campus_poll_system.dto;

public class OptionResultDto {
    private Long optionId;
    private String optionText;
    private long votesCount;
    private double percentage;

    public OptionResultDto() {
    }

    public OptionResultDto(Long optionId, String optionText, long votesCount, double percentage) {
        this.optionId = optionId;
        this.optionText = optionText;
        this.votesCount = votesCount;
        this.percentage = percentage;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public long getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(long votesCount) {
        this.votesCount = votesCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
