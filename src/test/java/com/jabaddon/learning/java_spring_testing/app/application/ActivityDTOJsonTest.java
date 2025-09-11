package com.jabaddon.learning.java_spring_testing.app.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.*;

@JsonTest
class ActivityDTOJsonTest {

    @Autowired
    private JacksonTester<ActivityDTO> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeActivityDTO() throws Exception {
        ActivityDTO dto = new ActivityDTO(
                1L,
                "Swimming",
                45L,
                LocalDate.of(2024, 3, 15)
        );

        JsonContent<ActivityDTO> write = json.write(dto);
        assertThat(write)
                .hasJsonPathNumberValue("$.id")
                .hasJsonPathStringValue("$.name")
                .hasJsonPathNumberValue("$.minutes")
                .doesNotHaveJsonPath("$.unknownField");
    }

    @Test
    void shouldDeserializeActivityDTO() throws Exception {
        String content = """
                {
                    "id": 2,
                    "name": "Cycling",
                    "minutes": 60,
                    "date": "2024-03-16"
                }
                """;

        ActivityDTO expected = new ActivityDTO(
                2L,
                "Cycling",
                60L,
                LocalDate.of(2024, 3, 16)
        );

        assertThat(json.parse(content)).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldHandleNullIdAndName() throws Exception {
        ActivityDTO dto = new ActivityDTO(
                null,
                null,
                30L,
                LocalDate.of(2024, 3, 17)
        );

        assertThat(json.write(dto)).isStrictlyEqualToJson("""
                {
                    "name": null,
                    "minutes": 30,
                    "date": "2024-03-17"
                }
                """);
    }

    @Test
    void shouldHandleZeroMinutes() throws Exception {
        ActivityDTO dto = new ActivityDTO(
                3L,
                "Rest Day",
                0L,
                LocalDate.of(2024, 3, 18)
        );

        assertThat(json.write(dto)).isStrictlyEqualToJson("""
                {
                    "id": 3,
                    "name": "Rest Day",
                    "minutes": 0,
                    "date": "2024-03-18"
                }
                """);
    }

    @Test
    void shouldSerializeActivityDTOWithJSONAssert() throws Exception {
        ActivityDTO dto = new ActivityDTO(
                4L,
                "Running",
                30L,
                LocalDate.of(2024, 3, 20)
        );

        String actualJson = objectMapper.writeValueAsString(dto);
        String expectedJson = """
                {
                    "id": 4, 
                    "minutes": 30,
                     "name": "Running",
                    "date": "2024-03-20"
                }
                """;

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldSerializeActivityDTOWithJsonPath() throws Exception {
        ActivityDTO dto = new ActivityDTO(
                5L,
                "Yoga",
                60L,
                LocalDate.of(2024, 3, 25)
        );

        String actualJson = objectMapper.writeValueAsString(dto);

        DocumentContext documentContext = JsonPath.parse(actualJson);

        assertThat(documentContext.<Integer>read("$.id")).isEqualTo(5);
        assertThat(documentContext.<String>read("$.name")).isEqualTo("Yoga");
        assertThat(documentContext.<Integer>read("$.minutes")).isEqualTo(60);
        assertThat(documentContext.<String>read("$.date")).isEqualTo("2024-03-25");

        org.hamcrest.MatcherAssert.assertThat(actualJson,
                hasJsonPath("$.id", is(equalTo(5))));
    }

    @Test
    void shouldSerializeListOfActivityDTOAndValidateWithHamcrest() throws Exception {
        List<ActivityDTO> activities = List.of(
                new ActivityDTO(1L, "Swimming", 45L, LocalDate.of(2024, 3, 15)),
                new ActivityDTO(2L, "Running", 30L, LocalDate.of(2024, 3, 16)),
                new ActivityDTO(3L, "Cycling", 60L, LocalDate.of(2024, 3, 17)),
                new ActivityDTO(4L, "Yoga", 75L, LocalDate.of(2024, 3, 18)),
                new ActivityDTO(5L, "Walking", 20L, LocalDate.of(2024, 3, 19))
        );

        String actualJson = objectMapper.writeValueAsString(activities);

        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$", hasSize(5)));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[0].id", is(equalTo(1))));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[0].name", is(equalTo("Swimming"))));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[0].minutes", is(equalTo(45))));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[0].date", is(equalTo("2024-03-15"))));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[0].date", matchesPattern("\\d{4}-\\d{2}-\\d{2}")));

        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[2].name", is(equalTo("Cycling"))));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[4].name", is(equalTo("Walking"))));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[4].minutes", is(equalTo(20))));

        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[*].name", 
                containsInAnyOrder("Swimming", "Running", "Cycling", "Yoga", "Walking")));
        org.hamcrest.MatcherAssert.assertThat(actualJson, hasJsonPath("$[*].minutes", 
                containsInAnyOrder(45, 30, 60, 75, 20)));
    }
}