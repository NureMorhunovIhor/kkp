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
public class MinMessage {
    private long id;
    private String text;
    @JsonProperty("has_media")
    private boolean hasMedia;
    private long date;
}
