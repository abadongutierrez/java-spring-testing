package com.jabaddon.learning.java_spring_testing.app.application;

import java.time.LocalDate;

public record ActivityDTO(
    Long id,
    String name,
    long minutes,
    LocalDate date
) {}