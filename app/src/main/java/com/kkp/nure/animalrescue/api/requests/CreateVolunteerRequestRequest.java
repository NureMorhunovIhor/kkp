package com.kkp.nure.animalrescue.api.requests;

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
public class CreateVolunteerRequestRequest {
    @JsonProperty("full_name")
    private String fullName;
    private String text;
    @JsonProperty("media_ids")
    private List<Long> mediaIds;
    @JsonProperty("has_vehicle")
    private boolean hasVehicle;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String city;
    private int availability;
    private int help;
    @JsonProperty("telegram_username")
    private String telegramUsername;
    @JsonProperty("viber_phone")
    private String viberPhone;
    @JsonProperty("whatsapp_phone")
    private String whatsappPhone;
}
