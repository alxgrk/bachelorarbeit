/*
 * Created on Aug 11, 2017
 *
 * author age
 */
package com.alxgrk.level3.util.booking;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.models.Timeslot;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonIgnoreType
public class ResourceUtils {

    private final Resource onResource;

    public static Stream<Timeslot> sortByBeginningTime(List<Timeslot> timeslots) {
        return timeslots.stream().sorted(Comparator.comparing(Timeslot::getBeginning).thenComparing(
                Comparator.comparing(Timeslot::getEnding)));
    }

    public CollisionType wouldCollide(Timeslot toBeBooked) {
        if (isTimeslotOverlappingWithAvailableTimeslots(toBeBooked)) {
            return CollisionType.NOT_AVAILABLE;
        } else if (isTimeslotOverlappingWithBookedTimeslots(toBeBooked)) {
            return CollisionType.ALREADY_BOOKED;
        } else {
            return CollisionType.NONE;
        }
    }

    public boolean isTimeslotEnclosedInAvailableTimeslots(Timeslot timeslotToCheck) {
        return onResource.getAvailableTimeslots()
                .stream()
                .anyMatch(t -> t.encloses(timeslotToCheck));
    }

    public boolean isTimeslotEnclosedInBookedTimeslots(Timeslot timeslotToCheck,
            List<Timeslot> timeslots) {
        return onResource.getBookedTimeslots()
                .stream()
                .anyMatch(t -> t.encloses(timeslotToCheck));
    }

    public boolean isTimeslotOverlappingWithAvailableTimeslots(Timeslot timeslotToCheck) {
        return onResource.getAvailableTimeslots()
                .stream()
                .anyMatch(t -> t.overlapsWith(timeslotToCheck));
    }

    public boolean isTimeslotOverlappingWithBookedTimeslots(Timeslot timeslotToCheck) {
        return onResource.getBookedTimeslots()
                .stream()
                .anyMatch(t -> t.overlapsWith(timeslotToCheck));
    }

}
