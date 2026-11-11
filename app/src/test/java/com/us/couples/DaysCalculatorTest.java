package com.us.couples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;

public class DaysCalculatorTest {

    @Test
    public void daysTogether_countsWholeDays() {
        assertEquals(10, DaysCalculator.daysTogether(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 11)));
    }

    @Test
    public void daysTogether_neverNegative() {
        assertEquals(0, DaysCalculator.daysTogether(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));
    }

    @Test
    public void nextMilestone_isNextHundred() {
        assertEquals(100, DaysCalculator.nextMilestone(0));
        assertEquals(100, DaysCalculator.nextMilestone(99));
        assertEquals(200, DaysCalculator.nextMilestone(100));
        assertEquals(500, DaysCalculator.nextMilestone(432));
    }

    @Test
    public void milestoneProgress_isPositionInBlock() {
        assertEquals(0, DaysCalculator.milestoneProgress(100));
        assertEquals(32, DaysCalculator.milestoneProgress(432));
    }

    @Test
    public void daysUntilAnniversary_laterThisYear() {
        assertEquals(30, DaysCalculator.daysUntilAnniversary(LocalDate.of(2024, 12, 10), LocalDate.of(2026, 11, 10)));
    }

    @Test
    public void daysUntilAnniversary_rollsToNextYear() {
        // anniversary was yesterday so the next one is 364 days out
        assertEquals(364, DaysCalculator.daysUntilAnniversary(LocalDate.of(2024, 11, 9), LocalDate.of(2026, 11, 10)));
    }

    @Test
    public void daysUntilAnniversary_today_isZero() {
        assertEquals(0, DaysCalculator.daysUntilAnniversary(LocalDate.of(2024, 11, 10), LocalDate.of(2026, 11, 10)));
    }
}
