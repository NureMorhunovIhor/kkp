package com.kkp.nure.animalrescue.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaType {
    PHOTO(1),
    VIDEO(2);

    public final int value;

    private MediaType(int value) {
        this.value = value;
    }

    @JsonValue
    public int toValue() {
        return value;
    }

    @JsonCreator
    public static MediaType fromValue(int value) {
        if(value == PHOTO.value)
            return PHOTO;
        if(value == VIDEO.value)
            return VIDEO;

        return null;
    }

}
