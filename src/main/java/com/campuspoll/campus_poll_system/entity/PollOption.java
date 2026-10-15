package com.campuspoll.campus_poll_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "poll_options")
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIgnore
    private Poll poll;

    @Column(name = "option_text", nullable = false, length = 255)
    private String optionText;

    public PollOption() {
    }

    public PollOption(String optionText) {
        this.optionText = optionText;
    }

    public PollOption(Poll poll, String optionText) {
        this.poll = poll;
        this.optionText = optionText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
