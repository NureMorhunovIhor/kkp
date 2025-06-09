package com.kkp.nure.animalrescue.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    REGULAR(0),
    VET(10),
    VOLUNTEER(11),
    VET_ADMIN(100),
    GLOBAL_ADMIN(999);

    public final int value;

    private UserRole(int value) {
        this.value = value;
    }

    @JsonValue
    public int toValue() {
        return value;
    }

    @JsonCreator
    public static UserRole fromValue(int value) {
        if(value == REGULAR.value)
            return REGULAR;
        if(value == VET.value)
            return VET;
        if(value == VOLUNTEER.value)
            return VOLUNTEER;
        if(value == VET_ADMIN.value)
            return VET_ADMIN;
        if(value == GLOBAL_ADMIN.value)
            return GLOBAL_ADMIN;

        return null;
    }
}
