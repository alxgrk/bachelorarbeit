package com.alxgrk.level3.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import com.alxgrk.level3.error.exceptions.timeslot.NoAvailableTimeslotsException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotAlreadyBookedException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsNotAvailableException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsToBookClashException;
import com.alxgrk.level3.util.booking.ResourceUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Entity
@Accessors(chain = true)
@Data
public class Resource implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "connectedResources", fetch = FetchType.EAGER)
    private Set<Account> administrators = new HashSet<>();

    @ElementCollection
    private List<Timeslot> availableTimeslots = new ArrayList<>();

    @ElementCollection
    private List<Timeslot> bookedTimeslots = new ArrayList<>();

    // -----
    // LOGIC
    // -----

    @Getter(AccessLevel.NONE)
    @Transient
    private transient final ResourceUtils utils = new ResourceUtils(this);

    // AVAILABILITY

    public Resource addAvailableTimeslot(@NonNull final Timeslot availableTimeslot) {
        List<Timeslot> copy = this.getAvailableTimeslots();
        copy.add(availableTimeslot);
        this.setAvailableTimeslots(copy);

        return this;
    }

    public Resource setAvailableTimeslots(@NonNull final List<Timeslot> availableTimeslots) {
        List<Timeslot> temp = Lists.newArrayList();

        ResourceUtils.sortByBeginningTime(availableTimeslots)
                .reduce(null, (earlier, later) -> {
                    if (earlier != null) {
                        if (earlier.overlapsWith(later) || earlier.abutsWith(later)) {
                            later.union(earlier);
                            temp.remove(earlier);
                        }
                    }
                    temp.add(later);
                    return later;
                });

        this.availableTimeslots = temp;

        return this;
    }

    // BOOKING

    public Resource addBookedTimeslotWithChecks(@NonNull final Timeslot bookedTimeslot)
            throws TimeslotAlreadyBookedException, NoAvailableTimeslotsException,
            TimeslotsNotAvailableException, TimeslotsToBookClashException {

        // do some consistency checks
        checkIfTimeslotOverlapsWithAlreadyBookedOnes(bookedTimeslot);

        List<Timeslot> copy = this.getBookedTimeslots();
        copy.add(bookedTimeslot);
        setBookedTimeslots(copy);

        return this;
    }

    public Resource setBookedTimeslots(@NonNull final List<Timeslot> bookedTimeslots)
            throws NoAvailableTimeslotsException, TimeslotsNotAvailableException,
            TimeslotsToBookClashException {

        // do some consistency checks
        checkIfThereAreAnyAvailableTimeslots();
        checkIfAllTimeslotsAreAvailable(bookedTimeslots);
        checkIfAnyTimeslotsOverlap(bookedTimeslots);

        this.bookedTimeslots = ResourceUtils.sortByBeginningTime(bookedTimeslots)
                .collect(Collectors.toList());

        return this;
    }

    // CHECKS

    @VisibleForTesting
    protected void checkIfTimeslotOverlapsWithAlreadyBookedOnes(Timeslot bookedTimeslot)
            throws TimeslotAlreadyBookedException {
        for (Timeslot t : this.getBookedTimeslots()) {
            if (t.overlapsWith(bookedTimeslot)) {
                throw new TimeslotAlreadyBookedException(bookedTimeslot);
            }
        }
    }

    @VisibleForTesting
    protected void checkIfThereAreAnyAvailableTimeslots() throws NoAvailableTimeslotsException {
        if (getAvailableTimeslots().isEmpty())
            throw new NoAvailableTimeslotsException(this);
    }

    @VisibleForTesting
    protected void checkIfAllTimeslotsAreAvailable(List<Timeslot> timeslots)
            throws TimeslotsNotAvailableException {
        List<Timeslot> notAvailableTimeslots = new ArrayList<>();
        timeslots.stream()
                .forEach(t -> {
                    if (!utils.isTimeslotEnclosedInAvailableTimeslots(t))
                        notAvailableTimeslots.add(t);
                });
        if (!notAvailableTimeslots.isEmpty()) {
            throw new TimeslotsNotAvailableException(notAvailableTimeslots);
        }
    }

    @VisibleForTesting
    protected void checkIfAnyTimeslotsOverlap(List<Timeslot> timeslots)
            throws TimeslotsToBookClashException {
        List<Timeslot> clashingTimeslots = new ArrayList<>();
        ResourceUtils.sortByBeginningTime(timeslots)
                .reduce(null, (i, t) -> {
                    if (null != i) {
                        if (i.overlapsWith(t))
                            clashingTimeslots.add(t);
                    }
                    return t;
                });
        if (!clashingTimeslots.isEmpty()) {
            throw new TimeslotsToBookClashException(clashingTimeslots);
        }
    }

}
