package com.kkp.nure.animalrescue.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkp.nure.animalrescue.enums.AnimalGender;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAnimalReportRequest {
    @JsonProperty("animal_id")
    private Long animalId;
    private String name;
    private String breed;
    private String notes;
    private double latitude;
    private double longitude;
    @JsonProperty("media_ids")
    private List<Long> mediaIds;
    private AnimalGender gender;
}
