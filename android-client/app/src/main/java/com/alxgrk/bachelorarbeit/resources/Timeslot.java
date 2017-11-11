package com.alxgrk.bachelorarbeit.resources;

import com.alxgrk.bachelorarbeit.util.CalendarDeserializer;
import com.alxgrk.bachelorarbeit.util.CalendarSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Calendar;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Timeslot {

    @JsonIgnoreProperties({"nano", "dayOfYear", "chronology"})
    @JsonDeserialize(using = CalendarDeserializer.class)
    @JsonSerialize(using = CalendarSerializer.class)
    private Calendar beginning;

    @JsonIgnoreProperties({"nano", "dayOfYear", "chronology"})
    @JsonDeserialize(using = CalendarDeserializer.class)
    @JsonSerialize(using = CalendarSerializer.class)
    private Calendar ending;

}
