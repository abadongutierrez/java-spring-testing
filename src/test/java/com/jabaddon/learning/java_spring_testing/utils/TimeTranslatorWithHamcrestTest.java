package com.jabaddon.learning.java_spring_testing.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TimeTranslatorWithHamcrestTest {

    @Test
    void testMinuteConversionWithHamcrest() {
        assertThat(TimeTranslator.toMinutes("1m"), is(equalTo(1L)));
        assertThat(TimeTranslator.toMinutes("30m"), is(equalTo(30L)));
        assertThat(TimeTranslator.toMinutes("0m"), is(equalTo(0L)));
    }

    @Test
    void testHourConversionWithHamcrest() {
        assertThat(TimeTranslator.toMinutes("1h"), is(equalTo(60L)));
        assertThat(TimeTranslator.toMinutes("2h"), is(equalTo(120L)));
        assertThat(TimeTranslator.toMinutes("3h"), is(equalTo(180L)));
        assertThat(TimeTranslator.toMinutes("24h"), is(equalTo(1440L)));
    }

    @Test
    void testDayConversionWithHamcrest() {
        assertThat(TimeTranslator.toMinutes("1d"), is(equalTo(1440L))); // 24 * 60
        assertThat(TimeTranslator.toMinutes("2d"), is(equalTo(2880L))); // 2 * 24 * 60
        assertThat(TimeTranslator.toMinutes("3d"), is(equalTo(4320L))); // 3 * 24 * 60
    }

    @Test
    void testWeekConversionWithHamcrest() {
        assertThat(TimeTranslator.toMinutes("1w"), is(equalTo(10080L))); // 7 * 24 * 60
        assertThat(TimeTranslator.toMinutes("2w"), is(equalTo(20160L))); // 2 * 7 * 24 * 60
        assertThat(TimeTranslator.toMinutes("4w"), is(equalTo(40320L))); // 4 * 7 * 24 * 60
    }

    @Test
    void testLargeNumbersWithHamcrest() {
        assertThat(TimeTranslator.toMinutes("100m"), is(equalTo(100L)));
        assertThat(TimeTranslator.toMinutes("100h"), is(equalTo(6000L)));
        assertThat(TimeTranslator.toMinutes("100d"), is(equalTo(144000L)));
        assertThat(TimeTranslator.toMinutes("100w"), is(equalTo(1008000L)));
    }

    @Test
    void testWithWhitespaceWithHamcrest() {
        assertThat(TimeTranslator.toMinutes(" 1h "), is(equalTo(60L)));
        assertThat(TimeTranslator.toMinutes("  2h"), is(equalTo(120L)));
        assertThat(TimeTranslator.toMinutes("3h  "), is(equalTo(180L)));
    }

    @Test
    void testNullInputWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes(null)
        );
        assertThat(exception.getMessage(), is(equalTo("Time string cannot be null or empty")));
    }

    @Test
    void testEmptyInputWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("")
        );
        assertThat(exception.getMessage(), is(equalTo("Time string cannot be null or empty")));
    }

    @Test
    void testWhitespaceOnlyInputWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("   ")
        );
        assertThat(exception.getMessage(), is(equalTo("Time string cannot be null or empty")));
    }

    @Test
    void testTooShortInputWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("h")
        );
        assertThat(exception.getMessage(), is(equalTo("Invalid time format: h")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "xyz", "1s", "2y", "3x"})
    void testInvalidUnitsWithHamcrest(String input) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes(input));
        assertThat(exception, is(instanceOf(IllegalArgumentException.class)));
    }

    @Test
    void testInvalidNumberFormatWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("abch")
        );
        assertThat(exception.getMessage(), is(equalTo("Invalid number format in time string: abch")));
    }

    @Test
    void testDecimalNumberWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("1.5h")
        );
        assertThat(exception.getMessage(), is(equalTo("Invalid number format in time string: 1.5h")));
    }

    @Test
    void testNegativeNumbersWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("-1h")
        );
        assertThat(exception.getMessage(), is(equalTo("Time value cannot be negative: -1h")));
    }

    @Test
    void testZeroValuesWithHamcrest() {
        assertThat(TimeTranslator.toMinutes("0m"), is(equalTo(0L)));
        assertThat(TimeTranslator.toMinutes("0h"), is(equalTo(0L)));
        assertThat(TimeTranslator.toMinutes("0d"), is(equalTo(0L)));
        assertThat(TimeTranslator.toMinutes("0w"), is(equalTo(0L)));
    }

    @Test
    void testMixedCaseUnitsWithHamcrest() {
        // Should fail because we only accept lowercase units
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1H"));
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1D"));
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1W"));
        Exception exception4 = assertThrows(IllegalArgumentException.class, () -> TimeTranslator.toMinutes("1M"));

        assertThat(exception1, is(instanceOf(IllegalArgumentException.class)));
        assertThat(exception2, is(instanceOf(IllegalArgumentException.class)));
        assertThat(exception3, is(instanceOf(IllegalArgumentException.class)));
        assertThat(exception4, is(instanceOf(IllegalArgumentException.class)));
    }

    @Test
    void testNoUnitWithHamcrest() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("123")
        );
        assertThat(exception.getMessage(), containsString("Invalid time unit"));
    }

    @Test
    void testMultipleUnitsWithHamcrest() {
        // Should fail because we only accept single unit
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimeTranslator.toMinutes("1h2m")
        );
        assertThat(exception.getMessage(), is(equalTo("Invalid number format in time string: 1h2m")));
    }
}
