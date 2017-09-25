/*
 * Created on Jun 20, 2017
 *
 * author age
 */
package com.alxgrk.level3.models;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;

import com.alxgrk.level3.error.exceptions.timeslot.TimeslotAlreadyBookedException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsNotAvailableException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsToBookClashException;
import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.models.Timeslot;
import com.google.common.collect.Lists;

public class Resource0Test {

    private Resource uut;

    private Timeslot availableTimeslot;

    private Timeslot bookedTimeslot;

    private LocalDateTime beginningAvailable;

    private LocalDateTime endingAvailable;

    private LocalDateTime beginningBooked;

    private LocalDateTime endingBooked;

    @Before
    public void setUp() throws Exception {

        beginningAvailable = LocalDateTime.of(2000, Month.JANUARY, 1, 12, 0);
        endingAvailable = LocalDateTime.of(2200, Month.JANUARY, 1, 12, 0);

        availableTimeslot = new Timeslot(beginningAvailable, endingAvailable);

        beginningBooked = LocalDateTime.of(2100, Month.JANUARY, 1, 12, 0);
        endingBooked = LocalDateTime.of(2102, Month.JANUARY, 1, 12, 0);
        bookedTimeslot = new Timeslot(beginningBooked, endingBooked);

        uut = new Resource().setName("garage");
    }

    // --- AVAILABLE TIMESLOTS ---

    @Test
    public void testSetAvailableTimeslots_addOne() throws Exception {
        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot));

        assertThat(uut.getAvailableTimeslots()).contains(availableTimeslot);
    }

    @Test
    public void testSetAvailableTimeslots_addTwo_disjoint() throws Exception {
        Timeslot availableDisjointTimeslot = new Timeslot(LocalDateTime.of(2201, Month.JANUARY, 1,
                12, 0), LocalDateTime.of(2202, Month.JANUARY, 1, 12, 0));

        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot, availableDisjointTimeslot));

        assertThat(uut.getAvailableTimeslots()).containsSequence(availableTimeslot,
                availableDisjointTimeslot);
    }

    @Test
    public void testSetAvailableTimeslots_addTwo_overlapping() throws Exception {
        Timeslot availableOverlappingTimeslot = new Timeslot(LocalDateTime.of(2150, Month.JANUARY,
                1, 12, 0), LocalDateTime.of(2202, Month.JANUARY, 1, 12, 0));
        Timeslot expected = new Timeslot(beginningAvailable, LocalDateTime.of(2202, Month.JANUARY,
                1, 12, 0));

        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot,
                availableOverlappingTimeslot));

        assertThat(uut.getAvailableTimeslots()).contains(expected);
    }

    @Test
    public void testSetAvailableTimeslots_addTwo_abuting() throws Exception {
        Timeslot availableAbutingTimeslot = new Timeslot(endingAvailable, LocalDateTime.of(2202,
                Month.JANUARY, 1, 12, 0));
        Timeslot expected = new Timeslot(beginningAvailable, LocalDateTime.of(2202, Month.JANUARY,
                1, 12, 0));

        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot, availableAbutingTimeslot));

        assertThat(uut.getAvailableTimeslots()).contains(expected);
    }

    @Test
    public void testSetAvailableTimeslots_addThree_unorderedAndOverlapping() throws Exception {
        Timeslot availableOverlappingTimeslot = new Timeslot(LocalDateTime.of(2150, Month.JANUARY,
                1, 12, 0), LocalDateTime.of(2202, Month.JANUARY, 1, 12, 0));
        Timeslot availableDisjointTimeslot = new Timeslot(LocalDateTime.of(1990, Month.JANUARY, 1,
                12, 0), LocalDateTime.of(1999, Month.JANUARY, 1, 12, 0));
        Timeslot expected = new Timeslot(beginningAvailable, LocalDateTime.of(2202, Month.JANUARY,
                1, 12, 0));

        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot, availableDisjointTimeslot,
                availableOverlappingTimeslot));

        assertThat(uut.getAvailableTimeslots()).containsSequence(availableDisjointTimeslot,
                expected);
    }

    @Test
    public void testAddAvailableTimeslot() throws Exception {

        uut.addAvailableTimeslot(availableTimeslot);

        assertThat(uut.getAvailableTimeslots()).containsExactly(availableTimeslot);
    }

    // --- BOOKED TIMESLOTS ---

    @Test(expected = TimeslotException.class)
    public void testSetBookedTimeslots_setOne_nonAvailable() throws Exception {
        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot));
    }

    @Test(expected = TimeslotsNotAvailableException.class)
    public void testSetBookedTimeslots_setOne_notInAvailableTimeslots() throws Exception {
        Timeslot availableEarlierTimeslot = new Timeslot(beginningAvailable, LocalDateTime.of(2050,
                Month.JANUARY, 1, 12, 0));
        uut.setAvailableTimeslots(Lists.newArrayList(availableEarlierTimeslot));

        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot));
    }

    @Test(expected = TimeslotsNotAvailableException.class)
    public void testSetBookedTimeslots_setOne_notCompletelyInAvailableTimeslots() throws Exception {
        Timeslot availableOverlappingTimeslot = new Timeslot(beginningAvailable, LocalDateTime.of(
                2101, Month.JANUARY, 1, 12, 0));
        uut.setAvailableTimeslots(Lists.newArrayList(availableOverlappingTimeslot));

        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot));
    }

    @Test
    public void testSetBookedTimeslots_setOne_inAvailableTimeslots() throws Exception {
        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot));

        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot));

        assertThat(uut.getBookedTimeslots()).containsExactly(bookedTimeslot);
    }

    @Test(expected = TimeslotsToBookClashException.class)
    public void testSetBookedTimeslots_clashingTimeslots() throws Exception {
        Timeslot bookedClashingTimeslot = new Timeslot(beginningBooked, endingBooked);
        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot));

        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot, bookedClashingTimeslot));
    }

    @Test(expected = TimeslotAlreadyBookedException.class)
    public void testAddBookedTimeslots_addOne_oneSetBefore_overlapping() throws Exception {
        Timeslot bookedOverlappingTimeslot = new Timeslot(LocalDateTime.of(2101, Month.JANUARY, 1,
                12, 0), endingBooked);
        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot));
        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot));

        uut.addBookedTimeslotWithChecks(bookedOverlappingTimeslot);
    }

    @Test
    public void testAddBookedTimeslots_addOne_oneSetBefore_ok() throws Exception {
        Timeslot bookedDisjointTimeslot = new Timeslot(LocalDateTime.of(2001, Month.JANUARY, 1,
                12, 0), LocalDateTime.of(2002, Month.JANUARY, 1, 12, 0));
        uut.setAvailableTimeslots(Lists.newArrayList(availableTimeslot));
        uut.setBookedTimeslots(Lists.newArrayList(bookedTimeslot));

        uut.addBookedTimeslotWithChecks(bookedDisjointTimeslot);

        assertThat(uut.getBookedTimeslots()).containsSequence(bookedDisjointTimeslot,
                bookedTimeslot);
    }

    // NULL CHECKS

    @Test
    public void testAddAvailableTimeslot_null() throws Exception {
        assertThatThrownBy(() -> uut.addAvailableTimeslot(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSetAvailableTimeslots_null() throws Exception {
        assertThatThrownBy(() -> uut.setAvailableTimeslots(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testAddBookedTimeslotWithChecks_null() throws Exception {
        assertThatThrownBy(() -> uut.addBookedTimeslotWithChecks(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSetBookedTimeslots_null() throws Exception {
        assertThatThrownBy(() -> uut.setBookedTimeslots(null))
                .isInstanceOf(NullPointerException.class);
    }
}
