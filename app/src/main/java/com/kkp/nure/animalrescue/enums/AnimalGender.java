package com.kkp.nure.animalrescue.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AnimalGender {
    UNKNOWN(0, "Unknown"),
    MALE(1, "Male"),
    FEMALE(2, "Female");

    public final int value;
    public final String strName;

    AnimalGender(int value, String name) {
        this.value = value;
        this.strName = name;
    }

    @JsonValue
    public int toValue() {
        return value;
    }

    @JsonCreator
    public static AnimalGender fromValue(int value) {
        if(value == UNKNOWN.value)
            return UNKNOWN;
        if(value == MALE.value)
            return MALE;
        if(value == FEMALE.value)
            return FEMALE;

        return null;
    }
}
