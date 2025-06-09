package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoundReport {
    private long id;
    @JsonProperty("reported_by")
    private BasicUser reportedBy;
    @JsonProperty("assigned_to")
    private BasicUser assignedTo;
    private Animal animal;
    @JsonProperty("created_at")
    private long createdAt;
    private String notes;
    private List<Media> media;
    private GeoPointInto location;
}
