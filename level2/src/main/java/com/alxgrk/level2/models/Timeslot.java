/*
 * Created on May 16, 2017
 *
 * author age
 */
package com.alxgrk.level2.models;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.threeten.extra.Interval;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Embeddable
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Data
public class Timeslot implements Serializable {

    @NonNull
    private LocalDateTime beginning;

    @NonNull
    private LocalDateTime ending;

    @Transient
    public Interval getInterval() {
        return Interval.of(toInstant(beginning), toInstant(ending));
    }

    public Timeslot union(@NonNull Timeslot another) {
        LocalDateTime newBeginning = getBeginning().isBefore(another.getBeginning())
                ? getBeginning() : another.getBeginning();
        LocalDateTime newEnding = getEnding().isAfter(another.getEnding()) ? getEnding()
                : another.getEnding();

        this.setBeginning(newBeginning);
        this.setEnding(newEnding);

        return this;
    }

    public boolean encloses(@NonNull Timeslot timeslotToCheck) {
        Interval thisInterval = this.getInterval();
        Interval thatInterval = timeslotToCheck.getInterval();
        return thisInterval.encloses(thatInterval);
    }

    public boolean overlapsWith(@NonNull Timeslot timeslotToCheck) {
        Interval thisInterval = this.getInterval();
        Interval thatInterval = timeslotToCheck.getInterval();
        return thisInterval.overlaps(thatInterval);
    }

    public boolean abutsWith(@NonNull Timeslot timeslotToCheck) {
        Interval thisInterval = this.getInterval();
        Interval thatInterval = timeslotToCheck.getInterval();
        return thisInterval.abuts(thatInterval);
    }

    private Instant toInstant(LocalDateTime ldt) {
        return ldt.toInstant(OffsetDateTime.now().getOffset());
    }
}
