package com.jabaddon.learning.java_spring_testing.app.domain.model;

import java.time.LocalDate;

public class Activity {
    private Long id;
    private String name;
    private long minutes;
    private LocalDate date;

    public Activity(String name, long minutes, LocalDate date) {
        validateActivity(name, minutes, date);
        this.name = name;
        this.minutes = minutes;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public long getMinutes() {
        return minutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void update(String newName, long newMinutes, LocalDate newDate) {
        validateActivity(newName, newMinutes, newDate);
        this.name = newName;
        this.minutes = newMinutes;
        this.date = newDate;
    }

    private void validateActivity(String name, long minutes, LocalDate date) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity name cannot be null or empty");
        }
        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
    }
}
