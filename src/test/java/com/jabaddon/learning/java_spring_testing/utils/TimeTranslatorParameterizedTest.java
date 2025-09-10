package com.jabaddon.learning.java_spring_testing.utils;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TimeTranslatorParameterizedTest {
    @ParameterizedTest
    @CsvSource({
            "1m, 1", "30m, 30", "0m, 0",
            "1h, 60", "2h, 120", "3h, 180", "24h, 1440",
            "1d, 1440", "2d, 2880", "3d, 4320"
    })
    void testMinuteConversionParameterizedWithCsvSource(String input, int expected) {
        assertEquals(expected, TimeTranslator.toMinutes(input));
    }

    @ParameterizedTest
    @MethodSource("valuesProvider")
    void testMinuteConversionParameterizedWithMethodSource(String input, int expected) {
        assertEquals(expected, TimeTranslator.toMinutes(input));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(Arguments.arguments("1m", 1), Arguments.arguments("3d", 4320));
    }

    @ParameterizedTest
    @FieldSource("VALUES_FROM_FIELD")
    void testMinuteConversionParameterizedWithFieldSource(String input, int expected) {
        assertEquals(expected, TimeTranslator.toMinutes(input));
    }

    static final Arguments[] VALUES_FROM_FIELD = { Arguments.arguments("1m", 1), Arguments.arguments("10m", 10) };

    @ParameterizedTest(name = "{index} => the input {0} should be transformed to {1} minutes")
    @ArgumentsSource(ArgumentsProvider.class)
    void testMinuteConversionParameterizedWithArgumentsSource(String input, int expected) {
        assertEquals(expected, TimeTranslator.toMinutes(input));
    }
}

class ArgumentsProvider implements org.junit.jupiter.params.provider.ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(Arguments.arguments("1m", 1), Arguments.arguments("3d", 4320));
    }
}
