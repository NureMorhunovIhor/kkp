package com.kkp.nure.animalrescue.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AnimalStatus {
    UNKNOWN(0, "Unknown"),
    FOUND(1, "Found"),
    ON_TREATMENT(2, "On treatment"),
    RELEASED(3, "Released"),
    WAITING_FOR_ADOPTION(4, "Waiting for adoption"),
    ADOPTED(5, "Adopted");

    public final int value;
    public final String strName;

    AnimalStatus(int value, String name) {
        this.value = value;
        this.strName = name;
    }

    @JsonValue
    public int toValue() {
        return value;
    }

    @JsonCreator
    public static AnimalStatus fromValue(int value) {
        if(value == UNKNOWN.value)
            return UNKNOWN;
        if(value == FOUND.value)
            return FOUND;
        if(value == ON_TREATMENT.value)
            return ON_TREATMENT;
        if(value == RELEASED.value)
            return RELEASED;
        if(value == WAITING_FOR_ADOPTION.value)
            return WAITING_FOR_ADOPTION;
        if(value == ADOPTED.value)
            return ADOPTED;

        return null;
    }
}
