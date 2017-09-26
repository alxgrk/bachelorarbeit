/*
 * Created on Sep 3, 2017
 *
 * author age
 */
package com.alxgrk.level1.util.booking;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        JsonNode node = new ObjectMapper().readValue(p, JsonNode.class);

        if (node.isObject()) {
            int dayOfMonth = node.get("dayOfMonth").asInt();
            int year = node.get("year").asInt();
            int monthValue = node.get("monthValue").asInt();
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            int second = node.get("second").asInt();

            return LocalDateTime.of(year,
                    monthValue,
                    dayOfMonth,
                    hour,
                    minute,
                    second);
        } else {
            return null;
        }
    }

}
