/*
 * Created on Jun 20, 2017
 *
 * author age
 */
package com.alxgrk.level1.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import com.alxgrk.level1.models.Timeslot;

public class Timeslot0Test {

    private Timeslot uut;

    private LocalDateTime beginningUut;

    private LocalDateTime endingUut;

    @Before
    public void setUp() throws Exception {
        beginningUut = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0);
        endingUut = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 0);
        uut = new Timeslot(beginningUut, endingUut);
    }

    @Test
    public void testGetInterval() throws Exception {
        long expectedSeconds = 31536000; // SECONDS OF THE INTERVAL

        Interval actual = uut.getInterval();

        assertThat(actual.toDuration().getSeconds()).isEqualTo(expectedSeconds);
    }

    // TESTS UNION

    @Test
    public void testUnion_beginningEarlier_endingEarlier() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2016, Month.JANUARY, 1, 0, 0);
        LocalDateTime ending = LocalDateTime.of(2017, Month.DECEMBER, 1, 0, 0);
        Timeslot anotherTimeslot = new Timeslot(beginning, ending);

        uut.union(anotherTimeslot);

        assertThat(uut.getBeginning()).isEqualTo(beginning);
        assertThat(uut.getEnding()).isEqualTo(endingUut);
    }

    @Test
    public void testUnion_beginningEarlier_endingLater() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2016, Month.JANUARY, 1, 0, 0);
        LocalDateTime ending = LocalDateTime.of(2018, Month.DECEMBER, 1, 0, 0);
        Timeslot anotherTimeslot = new Timeslot(beginning, ending);

        uut.union(anotherTimeslot);

        assertThat(uut.getBeginning()).isEqualTo(beginning);
        assertThat(uut.getEnding()).isEqualTo(ending);
    }

    @Test
    public void testUnion_beginningLater_endingEarlier() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2017, Month.DECEMBER, 1, 0, 0);
        LocalDateTime ending = LocalDateTime.of(2017, Month.DECEMBER, 1, 0, 0);
        Timeslot anotherTimeslot = new Timeslot(beginning, ending);

        uut.union(anotherTimeslot);

        assertThat(uut.getBeginning()).isEqualTo(beginningUut);
        assertThat(uut.getEnding()).isEqualTo(endingUut);
    }

    @Test
    public void testUnion_beginningLater_endingLater() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2017, Month.DECEMBER, 1, 0, 0);
        LocalDateTime ending = LocalDateTime.of(2018, Month.DECEMBER, 1, 0, 0);
        Timeslot anotherTimeslot = new Timeslot(beginning, ending);

        uut.union(anotherTimeslot);

        assertThat(uut.getBeginning()).isEqualTo(beginningUut);
        assertThat(uut.getEnding()).isEqualTo(ending);
    }

    @Test
    public void testUnion_null() throws Exception {
        assertThatThrownBy(() -> uut.union(null)).isInstanceOf(NullPointerException.class);
    }

    // TESTS ENCLOSES

    @Test
    public void testEncloses_true() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 1);
        LocalDateTime ending = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 0);
        Timeslot enclosedTimeslot = new Timeslot(beginning, ending);

        boolean actual = uut.encloses(enclosedTimeslot);

        assertTrue(actual);
    }

    @Test
    public void testEncloses_false() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 1);
        LocalDateTime ending = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 1);
        Timeslot enclosedTimeslot = new Timeslot(beginning, ending);

        boolean actual = uut.encloses(enclosedTimeslot);

        assertFalse(actual);
    }

    @Test
    public void testEncloses_null() throws Exception {
        assertThatThrownBy(() -> uut.encloses(null)).isInstanceOf(NullPointerException.class);
    }

    // TESTS OVERLAPS WITH

    @Test
    public void testOverlapsWith_true() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0);
        LocalDateTime ending = LocalDateTime.of(2019, Month.JANUARY, 1, 0, 0);
        Timeslot overlappingTimeslot = new Timeslot(beginning, ending);

        boolean actual = uut.overlapsWith(overlappingTimeslot);

        assertTrue(actual);
    }

    @Test
    public void testOverlapsWith_false() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 1);
        LocalDateTime ending = LocalDateTime.of(2018, Month.DECEMBER, 31, 23, 59);
        Timeslot overlappingTimeslot = new Timeslot(beginning, ending);

        boolean actual = uut.overlapsWith(overlappingTimeslot);

        assertFalse(actual);
    }

    @Test
    public void testOverlapsWith_null() throws Exception {
        assertThatThrownBy(() -> uut.overlapsWith(null)).isInstanceOf(NullPointerException.class);
    }

    // TESTS ABUTS WITH

    @Test
    public void testAbutsWith_true() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 0);
        LocalDateTime ending = LocalDateTime.of(2019, Month.JANUARY, 1, 0, 0);
        Timeslot abutingTimeslot = new Timeslot(beginning, ending);

        boolean actual = uut.abutsWith(abutingTimeslot);

        assertTrue(actual);
    }

    @Test
    public void testAbutsWith_false() throws Exception {
        LocalDateTime beginning = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 1);
        LocalDateTime ending = LocalDateTime.of(2018, Month.DECEMBER, 31, 23, 59);
        Timeslot abutingTimeslot = new Timeslot(beginning, ending);

        boolean actual = uut.abutsWith(abutingTimeslot);

        assertFalse(actual);
    }

    @Test
    public void testAbutsWith_null() throws Exception {
        assertThatThrownBy(() -> uut.abutsWith(null)).isInstanceOf(NullPointerException.class);
    }
}
