package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.enums.AnimalGender;
import com.kkp.nure.animalrescue.enums.AnimalStatus;
import com.kkp.nure.animalrescue.enums.MediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Animal {
    private long id;
    private String name;
    private String breed;
    private String description;
    private PaginatedResponse<Media> media;
    @JsonProperty("current_location")
    private GeoPointInto currentLocation;
    @JsonProperty("updated_at")
    private long updatedAt;
    private boolean subscribed;
    private AnimalStatus status;
    private AnimalGender gender;
}
