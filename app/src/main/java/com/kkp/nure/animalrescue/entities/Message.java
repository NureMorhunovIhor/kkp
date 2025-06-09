package com.kkp.nure.animalrescue.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kkp.nure.animalrescue.utils.UnixTimestampDeserializer;
import com.kkp.nure.animalrescue.utils.UnixTimestampSerializer;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private long id;
    private Dialog dialog;
    private BasicUser author;
    private String text;
    private Media media;
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    @JsonSerialize(using = UnixTimestampSerializer.class)
    private Date date;
}
