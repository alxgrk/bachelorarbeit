package com.alxgrk.bachelorarbeit.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Calendar;

public class CalendarSerializer extends JsonSerializer<Calendar> {

    @Override
    public void serialize(Calendar c, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("dayOfMonth", c.get(Calendar.DAY_OF_MONTH));
        gen.writeNumberField("year", c.get(Calendar.YEAR));
        gen.writeNumberField("monthValue", c.get(Calendar.MONTH));
        gen.writeNumberField("hour", c.get(Calendar.HOUR_OF_DAY));
        gen.writeNumberField("minute", c.get(Calendar.MINUTE));
        gen.writeNumberField("second", c.get(Calendar.SECOND));
        gen.writeEndObject();
    }
}