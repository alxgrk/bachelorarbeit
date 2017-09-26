package com.alxgrk.level2.error.exceptions.timeslot;

import java.util.Collection;

import com.alxgrk.level2.models.Timeslot;

public class TimeslotsToBookClashException extends TimeslotException {

    public TimeslotsToBookClashException(Collection<Timeslot> times) {
        super("some of the following timeslots to be booked would collide: " + times);
    }

}
