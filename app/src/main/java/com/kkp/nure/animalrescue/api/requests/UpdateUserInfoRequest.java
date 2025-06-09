package com.kkp.nure.animalrescue.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoRequest {
    @JsonProperty("first_name")
    private String firstName = null;
    @JsonProperty("last_name")
    private String lastName = null;
    private String email = null;
    @JsonProperty("photo_id")
    private Long photoId = null;
    @JsonProperty("telegram_username")
    private String telegramUsername = null;
    @JsonProperty("viber_phone")
    private String viberPhone = null;
    @JsonProperty("whatsapp_phone")
    private String whatsappPhone = null;
}
