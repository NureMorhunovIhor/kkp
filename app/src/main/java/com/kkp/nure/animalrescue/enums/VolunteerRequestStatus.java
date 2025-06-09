package com.kkp.nure.animalrescue.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VolunteerRequestStatus {
    REQUESTED(1, "Requested"),
    APPROVED(2, "Approved"),
    REFUSED(3, "Refused");

    public final int value;
    public final String strName;

    VolunteerRequestStatus(int value, String name) {
        this.value = value;
        this.strName = name;
    }

    @JsonValue
    public int toValue() {
        return value;
    }

    @JsonCreator
    public static VolunteerRequestStatus fromValue(int value) {
        if(value == REQUESTED.value)
            return REQUESTED;
        if(value == APPROVED.value)
            return APPROVED;
        if(value == REFUSED.value)
            return REFUSED;

        return null;
    }
}
