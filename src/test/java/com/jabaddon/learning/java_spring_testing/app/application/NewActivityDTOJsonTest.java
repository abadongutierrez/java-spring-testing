package com.jabaddon.learning.java_spring_testing.app.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewActivityDTOJsonTest {

    @Autowired
    private JacksonTester<NewActivityDTO> json;

    @Test
    void shouldSerializeNewActivityDTO() throws Exception {
        NewActivityDTO dto = new NewActivityDTO(
                "Morning Run", 
                "07:00", 
                LocalDate.of(2024, 3, 15)
        );

        assertThat(json.write(dto)).isStrictlyEqualToJson("""
                {
                    "name": "Morning Run",
                    "time": "07:00",
                    "date": "2024-03-15"
                }
                """);
    }

    @Test
    void shouldDeserializeNewActivityDTO() throws Exception {
        String content = """
                {
                    "name": "Evening Workout",
                    "time": "18:30",
                    "date": "2024-03-16"
                }
                """;

        NewActivityDTO expected = new NewActivityDTO(
                "Evening Workout", 
                "18:30", 
                LocalDate.of(2024, 3, 16)
        );

        assertThat(json.parse(content)).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldHandleNullValues() throws Exception {
        NewActivityDTO dto = new NewActivityDTO(null, null, null);

        assertThat(json.write(dto)).isStrictlyEqualToJson("""
                {
                    "name": null,
                    "time": null,
                    "date": null
                }
                """);
    }
}