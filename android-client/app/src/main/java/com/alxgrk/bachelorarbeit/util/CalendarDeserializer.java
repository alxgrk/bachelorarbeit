package com.alxgrk.bachelorarbeit.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class CalendarDeserializer extends JsonDeserializer<Calendar> {

    @Override
    public Calendar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = new ObjectMapper().readValue(p, JsonNode.class);

        if (node.isObject()) {
            int dayOfMonth = node.get("dayOfMonth").asInt();
            int year = node.get("year").asInt();
            int monthValue = node.get("monthValue").asInt();
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            int second = node.get("second").asInt();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthValue, dayOfMonth, hour, minute, second);

            return calendar;
        } else {
            return null;
        }
    }

}