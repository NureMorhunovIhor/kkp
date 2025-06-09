package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DonationGoal {
    private long id;
    private String name;
    private String description;
    @JsonProperty("need_amount")
    private double needAmount;
    @JsonProperty("got_amount")
    private double gotAmount;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("ended_at")
    private Long endedAt;
}
