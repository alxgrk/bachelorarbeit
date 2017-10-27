package com.alxgrk.bachelorarbeit.util;

import android.util.Pair;

import com.alxgrk.bachelorarbeit.resources.Timeslot;

import java.util.Calendar;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeslotUtils {

    private static float MINIMUM_RELATIVE_LEFT = 0.48f;
    private static float MINIMUM_RELATIVE_RIGHT = 0.52f;

    public Pair<Float, Float> relativeTo(List<Timeslot> availables, List<Timeslot> booked) {
        Timeslot firstAvailable = availables.get(0);
        Timeslot lastAvailable = availables.get(availables.size() - 1);

        long availableBeginningInMillis = firstAvailable.getBeginning().getTimeInMillis();
        long availableEndingInMillis = lastAvailable.getEnding().getTimeInMillis();
        float intervalAvailable = availableEndingInMillis - availableBeginningInMillis;

        Timeslot firstBooked = booked.get(0);
        Timeslot lastBooked = booked.get(booked.size() - 1);

        long intervalBeginning = firstBooked.getBeginning().getTimeInMillis() - availableBeginningInMillis;
        float posBookedBeginning = intervalBeginning / intervalAvailable;

        long intervalEnding = availableEndingInMillis - lastBooked.getBeginning().getTimeInMillis();
        float posBookedEnding = 1 - intervalEnding / intervalAvailable;

        // FIXME remove 50
        if (50 < (posBookedEnding - posBookedBeginning)) {
            return Pair.create(posBookedBeginning, posBookedEnding);
        } else {
            return Pair.create(MINIMUM_RELATIVE_LEFT, MINIMUM_RELATIVE_RIGHT);
        }
    }

    public Pair<String, String> beginAndEndString(Timeslot toConvert) {
        String beginString = asDateString(toConvert.getBeginning());
        String endString = asDateString(toConvert.getEnding());

        return Pair.create(beginString, endString);
    }

    private String asDateString(Calendar calendar) {
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return dayOfMonth + "." + month + "." + year;
    }

}
