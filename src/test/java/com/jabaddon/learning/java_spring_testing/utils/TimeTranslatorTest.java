package com.jabaddon.learning.java_spring_testing.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeTranslatorTest {

    @Test
    void testMinuteConversion() {
        assertEquals(1, TimeTranslator.toMinutes("1m"));
        assertEquals(30, TimeTranslator.toMinutes("30m"));
        assertEquals(0, TimeTranslator.toMinutes("0m"));
    }

    @Test
    void testHourConversion() {
        assertEquals(60, TimeTranslator.toMinutes("1h"));
        assertEquals(120, TimeTranslator.toMinutes("2h"));
        assertEquals(180, TimeTranslator.toMinutes("3h"));
        assertEquals(1440, TimeTranslator.toMinutes("24h"));
    }

    @Test
    void testDayConversion() {
        assertEquals(1440, TimeTranslator.toMinutes("1d")); // 24 * 60
        assertEquals(2880, TimeTranslator.toMinutes("2d")); // 2 * 24 * 60
        assertEquals(4320, TimeTranslator.toMinutes("3d")); // 3 * 24 * 60
    }

    @Test
    void testWeekConversion() {
        assertEquals(10080, TimeTranslator.toMinutes("1w")); // 7 * 24 * 60
        assertEquals(20160, TimeTranslator.toMinutes("2w")); // 2 * 7 * 24 * 60
        assertEquals(40320, TimeTranslator.toMinutes("4w")); // 4 * 7 * 24 * 60
    }

    @Test
    void testLargeNumbers() {
        assertEquals(100, TimeTranslator.toMinutes("100m"));
        assertEquals(6000, TimeTranslator.toMinutes("100h"));
        assertEquals(144000, TimeTranslator.toMinutes("100d"));
        assertEquals(1008000, TimeTranslator.toMinutes("100w"));
    }

    @Test
    void testWithWhitespace() {
        assertEquals(60, TimeTranslator.toMinutes(" 1h "));
        assertEquals(120, TimeTranslator.toMinutes("  2h"));
        assertEquals(180, TimeTranslator.toMinutes("3h  "));
    }

    @Test
    void testNullInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes(null)
        );
        assertEquals("Time string cannot be null or empty", exception.getMessage());
    }

    @Test
    void testEmptyInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("")
        );
        assertEquals("Time string cannot be null or empty", exception.getMessage());
    }

    @Test
    void testWhitespaceOnlyInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("   ")
        );
        assertEquals("Time string cannot be null or empty", exception.getMessage());
    }

    @Test
    void testTooShortInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("h")
        );
        assertEquals("Invalid time format: h", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "xyz", "1s", "2y", "3x"})
    void testInvalidUnits(String input) {
        assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes(input));
    }

    @Test
    void testInvalidNumberFormat() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("abch")
        );
        assertEquals("Invalid number format in time string: abch", exception.getMessage());
    }

    @Test
    void testDecimalNumber() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("1.5h")
        );
        assertEquals("Invalid number format in time string: 1.5h", exception.getMessage());
    }

    @Test
    void testNegativeNumbers() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("-1h")
        );
        assertEquals("Time value cannot be negative: -1h", exception.getMessage());
    }

    @Test
    void testZeroValues() {
        assertEquals(0, TimeTranslator.toMinutes("0m"));
        assertEquals(0, TimeTranslator.toMinutes("0h"));
        assertEquals(0, TimeTranslator.toMinutes("0d"));
        assertEquals(0, TimeTranslator.toMinutes("0w"));
    }

    @Test
    void testMixedCaseUnits() {
        // Should fail because we only accept lowercase units
        assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1H"));
        assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1D"));
        assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1W"));
        assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1M"));
    }

    @Test
    void testNoUnit() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("123")
        );
        assertTrue(exception.getMessage().contains("Invalid time unit"));
    }

    @Test
    void testMultipleUnits() {
        // Should fail because we only accept single unit
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TimeTranslator.toMinutes("1h2m")
        );
        assertEquals("Invalid number format in time string: 1h2m", exception.getMessage());
    }
}

