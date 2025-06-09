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
public class TreatmentReport {
    private long id;
    @JsonProperty("animal_report")
    private FoundReport report;
    private String description;
    @JsonProperty("money_spent")
    private double moneySpent;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("vet_clinic")
    private VetClinic vetClinic;
}
