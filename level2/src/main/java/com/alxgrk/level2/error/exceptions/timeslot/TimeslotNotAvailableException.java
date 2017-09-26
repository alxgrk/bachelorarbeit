package com.alxgrk.level2.error.exceptions.timeslot;

import java.time.format.DateTimeFormatter;

import com.alxgrk.level2.models.Timeslot;

public class TimeslotNotAvailableException extends TimeslotException {

    public TimeslotNotAvailableException(Timeslot time) {
        super("the resource is already booked for the time between " + time.getBeginning().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " and " + time.getEnding().format(
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

}
