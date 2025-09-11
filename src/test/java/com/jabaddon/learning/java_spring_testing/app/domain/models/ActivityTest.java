package com.jabaddon.learning.java_spring_testing.app.domain.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivityTest {

    @Test
    @DisplayName("Should create activity with valid data and initialize properties correctly")
    void shouldCreateActivityWithValidData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Activity activity = new Activity("Swimming", 45L, yesterday);

        assertThat(activity.getName(), is(equalTo("Swimming")));
        assertThat(activity.getMinutes(), is(equalTo(45L)));
        assertThat(activity.getDate(), is(equalTo(yesterday)));
        assertThat(activity.getId(), is(nullValue()));
    }

    @Test
    @DisplayName("Should set and get ID correctly")
    void shouldSetAndGetId() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Activity activity = new Activity("Running", 30L, yesterday);

        activity.setId(123L);

        assertThat(activity.getId(), is(equalTo(123L)));
    }

    @Test
    @DisplayName("Should update activity properties with valid data")
    void shouldUpdateActivityWithValidData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        Activity activity = new Activity("Swimming", 45L, yesterday);

        activity.update("Cycling", 60L, twoDaysAgo);

        assertThat(activity.getName(), is(equalTo("Cycling")));
        assertThat(activity.getMinutes(), is(equalTo(60L)));
        assertThat(activity.getDate(), is(equalTo(twoDaysAgo)));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity(null, 45L, yesterday)
        );

        assertThat(exception.getMessage(), is(equalTo("Activity name cannot be null or empty")));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity("", 45L, yesterday)
        );

        assertThat(exception.getMessage(), is(equalTo("Activity name cannot be null or empty")));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity("   ", 45L, yesterday)
        );

        assertThat(exception.getMessage(), is(equalTo("Activity name cannot be null or empty")));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when minutes is negative")
    void shouldThrowExceptionWhenMinutesIsNegative() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity("Swimming", -1L, yesterday)
        );

        assertThat(exception.getMessage(), is(equalTo("Minutes cannot be negative")));
    }

    @Test
    @DisplayName("Should allow zero minutes for rest days")
    void shouldAllowZeroMinutes() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        Activity activity = new Activity("Rest Day", 0L, yesterday);

        assertThat(activity.getMinutes(), is(equalTo(0L)));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when date is null")
    void shouldThrowExceptionWhenDateIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity("Swimming", 45L, null)
        );

        assertThat(exception.getMessage(), is(equalTo("Date cannot be null")));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when date is in the future")
    void shouldThrowExceptionWhenDateIsInFuture() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity("Swimming", 45L, tomorrow)
        );

        assertThat(exception.getMessage(), is(equalTo("Date cannot be in the future")));
    }

    @Test
    @DisplayName("Should allow today's date for activities")
    void shouldAllowTodaysDate() {
        LocalDate today = LocalDate.now();

        Activity activity = new Activity("Swimming", 45L, today);

        assertThat(activity.getDate(), is(equalTo(today)));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with invalid name")
    void shouldThrowExceptionOnUpdateWithInvalidName() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Activity activity = new Activity("Swimming", 45L, yesterday);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> activity.update(null, 60L, yesterday)
        );

        assertThat(exception.getMessage(), is(equalTo("Activity name cannot be null or empty")));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with negative minutes")
    void shouldThrowExceptionOnUpdateWithNegativeMinutes() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Activity activity = new Activity("Swimming", 45L, yesterday);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> activity.update("Running", -10L, yesterday)
        );

        assertThat(exception.getMessage(), is(equalTo("Minutes cannot be negative")));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with future date")
    void shouldThrowExceptionOnUpdateWithFutureDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Activity activity = new Activity("Swimming", 45L, yesterday);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> activity.update("Running", 30L, tomorrow)
        );

        assertThat(exception.getMessage(), is(equalTo("Date cannot be in the future")));
    }
}