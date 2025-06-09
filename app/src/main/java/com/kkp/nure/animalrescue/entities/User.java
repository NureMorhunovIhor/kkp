package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkp.nure.animalrescue.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private UserRole role;
    @JsonProperty("mfa_enabled")
    private boolean mfaEnabled;
    private Media photo;
    @JsonProperty("telegram_username")
    private String telegramUsername;
    @JsonProperty("viber_phone")
    private String viberPhone;
    @JsonProperty("whatsapp_phone")
    private String whatsappPhone;
}
