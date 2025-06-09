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
public class VetClinic {
    private long id;
    private String name;
    private GeoPointInto location;
    private BasicUser admin;
    @JsonProperty("employees_count")
    private long employeesCount;
}
