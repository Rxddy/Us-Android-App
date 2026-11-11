package com.us.couples;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// day counting math for Our Days (has unit tests)
public final class DaysCalculator {
    public static final int MILESTONE_STEP = 100;

    private DaysCalculator() {
    }

    public static long daysTogether(LocalDate start, LocalDate today) {
        return Math.max(0, ChronoUnit.DAYS.between(start, today));
    }

    public static long nextMilestone(long days) {
        return (days / MILESTONE_STEP + 1) * MILESTONE_STEP;
    }

    public static int milestoneProgress(long days) {
        return (int) (days % MILESTONE_STEP);
    }

    public static long daysUntilAnniversary(LocalDate start, LocalDate today) {
        LocalDate next = start.withYear(today.getYear());
        if (next.isBefore(today)) {
            next = next.plusYears(1);
        }
        return ChronoUnit.DAYS.between(today, next);
    }
}
