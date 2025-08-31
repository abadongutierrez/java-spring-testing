package com.jabaddon.learning.java_spring_testing.app.application;

import java.time.LocalDate;

public record NewActivityDTO(
        String name,
        String time,
        LocalDate date
) {}
