package com.alxgrk.level2.error.exceptions.timeslot;

import com.alxgrk.level2.models.Resource;

public class NoAvailableTimeslotsException extends TimeslotException {

    public NoAvailableTimeslotsException(Resource resource) {
        super("The resource " + resource + " does not have any available timeslots.");
    }

}
