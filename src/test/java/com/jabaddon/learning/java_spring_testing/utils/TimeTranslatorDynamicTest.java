package com.jabaddon.learning.java_spring_testing.utils;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class TimeTranslatorDynamicTest {

    @TestFactory
    Stream<DynamicTest> dynamicTests() {
        return Stream.of(List.of("2m", 2L), List.of("3m", 3L), List.of("1d", 1440L))
                .map(v ->
                        dynamicTest(v.get(0) + " should be " + v.get(1) + " minutes", () -> {
                            assertThat(TimeTranslator.toMinutes((String) v.get(0)), is(v.get(1)));
                        })

                );
    }
}
