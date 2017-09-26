/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level1.rest.rto;

import java.time.LocalDateTime;

import com.alxgrk.level1.util.booking.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class TimeslotRto {

    @JsonIgnoreProperties({ "nano", "dayOfYear", "chronology" })
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime beginning;

    @JsonIgnoreProperties({ "nano", "dayOfYear", "chronology" })
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime ending;

}
