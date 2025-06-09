package com.kkp.nure.animalrescue.api.requests;

import com.kkp.nure.animalrescue.enums.MediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadMediaRequest {
    private MediaType type;
    private long size;
}
