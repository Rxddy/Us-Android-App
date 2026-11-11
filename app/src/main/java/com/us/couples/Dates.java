package com.us.couples;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.DateTimeParseException;

// small date helpers, dates are saved as yyyy-MM-dd strings
public final class Dates {
    private Dates() {
    }

    public static String today() {
        return LocalDate.now().toString();
    }

    public static LocalDate parse(String iso) {
        if (iso == null || iso.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(iso);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String pretty(String iso) {
        LocalDate date = parse(iso);
        if (date == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public static String iso(int year, int monthZeroBased, int day) {
        return LocalDate.of(year, monthZeroBased + 1, day).toString();
    }
}
