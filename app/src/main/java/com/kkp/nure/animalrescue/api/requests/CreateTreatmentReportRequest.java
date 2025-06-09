package com.kkp.nure.animalrescue.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTreatmentReportRequest {
    @JsonProperty("animal_report_id")
    private long reportId;
    private String description;
    @JsonProperty("money_spent")
    private double moneySpent;
}
