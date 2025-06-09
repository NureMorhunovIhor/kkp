package com.kkp.nure.animalrescue.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MfaResponse {
    @JsonProperty("mfa_token")
    private String mfaToken;
    @JsonProperty("expires_at")
    private long expiresAt;
}
