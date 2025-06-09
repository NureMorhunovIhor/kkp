package com.kkp.nure.animalrescue.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeoPointInto {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
}
