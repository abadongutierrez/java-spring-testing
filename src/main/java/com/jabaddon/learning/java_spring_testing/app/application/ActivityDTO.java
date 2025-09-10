package com.jabaddon.learning.java_spring_testing.app.application;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ActivityDTO(
    @JsonInclude(JsonInclude.Include.NON_NULL) Long id,
    String name,
    long minutes,
    LocalDate date
) {}