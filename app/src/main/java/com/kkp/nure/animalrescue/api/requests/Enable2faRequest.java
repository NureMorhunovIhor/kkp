package com.kkp.nure.animalrescue.api.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Enable2faRequest {
    private String password;
    private String key;
    private String code;
}
