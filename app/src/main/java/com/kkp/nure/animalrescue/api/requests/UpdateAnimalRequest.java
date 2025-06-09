package com.kkp.nure.animalrescue.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkp.nure.animalrescue.enums.AnimalGender;
import com.kkp.nure.animalrescue.enums.AnimalStatus;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnimalRequest {
    private String name = null;
    private String breed = null;
    private AnimalStatus status = null;
    private String description = null;
    @JsonProperty("add_media_ids")
    private List<Integer> addMediaIds = null;
    @JsonProperty("remove_media_ids")
    private List<Integer> removeMediaIds = null;
    @JsonProperty("current_latitude")
    private Double currentLatitude = null;
    @JsonProperty("current_longitude")
    private Double currentLongitude = null;
    private AnimalGender gender = null;
}
