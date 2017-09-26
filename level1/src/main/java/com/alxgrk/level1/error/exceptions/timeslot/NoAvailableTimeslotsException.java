package com.alxgrk.level1.error.exceptions.timeslot;

import com.alxgrk.level1.models.Resource;

public class NoAvailableTimeslotsException extends TimeslotException {

    public NoAvailableTimeslotsException(Resource resource) {
        super("The resource " + resource + " does not have any available timeslots.");
    }

}
