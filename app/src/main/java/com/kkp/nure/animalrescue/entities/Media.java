package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kkp.nure.animalrescue.enums.MediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Media {
    private long id;
    private MediaType type;
    private String url;
}
