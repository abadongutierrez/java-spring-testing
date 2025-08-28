package com.jabaddon.learning.java_spring_testing.utils;

public class TimeTranslator {

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_IN_WEEK = 7;

    public static long toMinutes(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }

        timeString = timeString.trim();

        if (timeString.length() < 2) {
            throw new IllegalArgumentException("Invalid time format: " + timeString);
        }

        // Extract the unit (last character)
        char unit = timeString.charAt(timeString.length() - 1);
        String numberPart = timeString.substring(0, timeString.length() - 1);

        // Parse the number
        long number;
        try {
            number = Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in time string: " + timeString);
        }

        if (number < 0) {
            throw new IllegalArgumentException("Time value cannot be negative: " + timeString);
        }

        // Convert to minutes based on unit
        switch (unit) {
            case 'm':
                return number;
            case 'h':
                return number * MINUTES_IN_HOUR;
            case 'd':
                return number * HOURS_IN_DAY * MINUTES_IN_HOUR;
            case 'w':
                return number * DAYS_IN_WEEK * HOURS_IN_DAY * MINUTES_IN_HOUR;
            default:
                throw new IllegalArgumentException("Invalid time unit '" + unit + "'. Valid units are: w, d, h, m");
        }
    }
}
