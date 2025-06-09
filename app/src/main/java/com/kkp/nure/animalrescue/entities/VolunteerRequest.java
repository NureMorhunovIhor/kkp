package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkp.nure.animalrescue.enums.VolunteerRequestStatus;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerRequest {
    public static final int AVAILABILITY_WEEKDAYS = 1 << 0;
    public static final int AVAILABILITY_WEEKENDS = 1 << 1;

    public static final int HELP_SHELTER = 1 << 0;
    public static final int HELP_CLINIC_DELIVERY = 1 << 1;
    public static final int HELP_ONSITE_VISIT = 1 << 2;
    public static final int HELP_MEDICAL_CARE = 1 << 3;
    public static final int HELP_INFORMATION = 1 << 4;

    private String id;
    private BasicUser user;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("reviewed_at")
    private Long reviewedAt;
    private String text;
    @JsonProperty("review_text")
    private String reviewText;
    private List<Media> medias;
    private VolunteerRequestStatus status;
    @JsonProperty("full_name")
    private String fullName;
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
