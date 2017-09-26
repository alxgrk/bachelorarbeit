package com.alxgrk.level1.error.exceptions.timeslot;

import java.util.Collection;

import com.alxgrk.level1.models.Timeslot;

public class TimeslotsNotAvailableException extends TimeslotException {

    public TimeslotsNotAvailableException(Collection<Timeslot> times) {
        super("the resource is already booked for the following times: " + times);
    }

}
