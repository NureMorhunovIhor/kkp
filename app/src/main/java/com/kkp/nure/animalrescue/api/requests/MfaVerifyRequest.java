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
public class MfaVerifyRequest {
    @JsonProperty("mfa_code")
    private String mfaCode;
    @JsonProperty("mfa_token")
    private String mfaToken;
}
