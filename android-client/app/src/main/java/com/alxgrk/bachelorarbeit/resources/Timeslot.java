package com.alxgrk.bachelorarbeit.resources;

import com.alxgrk.bachelorarbeit.util.CalendarDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;
import java.util.Calendar;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Timeslot {

    @JsonIgnoreProperties({ "nano", "dayOfYear", "chronology" })
    @JsonDeserialize(using = CalendarDeserializer.class)
    private Calendar beginning;

    @JsonIgnoreProperties({ "nano", "dayOfYear", "chronology" })
    @JsonDeserialize(using = CalendarDeserializer.class)
    private Calendar ending;

}
