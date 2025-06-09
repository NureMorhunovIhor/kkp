package com.kkp.nure.animalrescue.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Donation {
    private long id;
    private BasicUser user;
    private double amount;
    private long date;
    private String comment;
    private DonationGoal goal;
}
