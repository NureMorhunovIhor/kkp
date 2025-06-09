package com.kkp.nure.animalrescue.api.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonationRequest {
    private double amount;
    private boolean anonymous;
    private String comment;
}
