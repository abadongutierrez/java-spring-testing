package com.jabaddon.learning.java_spring_testing.examples;

import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.utils.TimeTranslator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Better Assertions Demo: JUnit vs Hamcrest vs AssertJ")
public class BetterAssertionsDemoTest {

    @Nested
    @DisplayName("String Assertions Comparison")
    class StringAssertions {

        @Test
        @DisplayName("String equality and content checks")
        void stringAssertions() {
            String activityName = "Morning Run";
            
            // JUnit
            assertEquals("Morning Run", activityName);
            assertTrue(activityName.contains("Run"));
            assertTrue(activityName.startsWith("Morning"));
            assertFalse(activityName.isEmpty());
            
            // Hamcrest
            MatcherAssert.assertThat(activityName, is("Morning Run"));
            MatcherAssert.assertThat(activityName, containsString("Run"));
            MatcherAssert.assertThat(activityName, startsWith("Morning"));
            MatcherAssert.assertThat(activityName, not(emptyString()));
            
            // AssertJ
            Assertions.assertThat(activityName)
                .isEqualTo("Morning Run")
                .contains("Run")
                .startsWith("Morning")
                .isNotEmpty()
                .hasSize(11);
        }

        @Test
        @DisplayName("String null and empty checks")
        void nullAndEmptyChecks() {
            String nullString = null;
            String emptyString = "";
            String blankString = "   ";
            
            // JUnit
            assertNull(nullString);
            assertTrue(emptyString.isEmpty());
            assertTrue(blankString.isBlank());
            
            // Hamcrest
            MatcherAssert.assertThat(nullString, nullValue());
            MatcherAssert.assertThat(emptyString, emptyString());
            MatcherAssert.assertThat(blankString, blankString());
            MatcherAssert.assertThat("some string", not(anyOf(blankString(), emptyString(), nullValue())));
            
            // AssertJ
            Assertions.assertThat(nullString).isNull();
            Assertions.assertThat(emptyString).isEmpty();
            Assertions.assertThat(blankString).isBlank();
        }
    }

    @Nested
    @DisplayName("Numeric Assertions Comparison")
    class NumericAssertions {

        @Test
        @DisplayName("Numeric comparisons and ranges")
        void numericComparisons() {
            long minutes = TimeTranslator.toMinutes("2h");
            
            // JUnit
            assertEquals(120, minutes);
            assertTrue(minutes > 100);
            assertTrue(minutes >= 120);
            assertTrue(minutes < 200);
            
            // Hamcrest
            MatcherAssert.assertThat(minutes, is(120L));
            MatcherAssert.assertThat(minutes, greaterThan(100L));
            MatcherAssert.assertThat(minutes, greaterThanOrEqualTo(120L));
            MatcherAssert.assertThat(minutes, lessThan(200L));
            MatcherAssert.assertThat(minutes, both(greaterThan(100L)).and(lessThan(200L)));
            
            // AssertJ
            Assertions.assertThat(minutes)
                .isEqualTo(120L)
                .isGreaterThan(100L)
                .isGreaterThanOrEqualTo(120L)
                .isLessThan(200L)
                .isBetween(100L, 200L);
        }
    }

    @Nested
    @DisplayName("Object Assertions Comparison")
    class ObjectAssertions {

        @Test
        @DisplayName("Object equality and property checks")
        void objectAssertions() {
            Activity activity = new Activity("Yoga Session", 60, LocalDate.of(2024, 1, 15));
            ActivityDTO dto = new ActivityDTO(1L, "Yoga Session", 60, LocalDate.of(2024, 1, 15));
            
            // JUnit
            assertNotNull(activity);
            assertEquals("Yoga Session", activity.getName());
            assertEquals(60, activity.getMinutes());
            assertEquals(LocalDate.of(2024, 1, 15), activity.getDate());
            
            // Hamcrest
            MatcherAssert.assertThat(activity, notNullValue());
            MatcherAssert.assertThat(activity, hasProperty("name", is("Yoga Session")));
            MatcherAssert.assertThat(activity, allOf(
                hasProperty("name", is("Yoga Session")),
                hasProperty("minutes", is(60L)),
                hasProperty("date", is(LocalDate.of(2024, 1, 15)))
            ));
            
            // AssertJ
            Assertions.assertThat(activity)
                .isNotNull()
                .extracting("name", "minutes", "date")
                .containsExactly("Yoga Session", 60L, LocalDate.of(2024, 1, 15));
                
            Assertions.assertThat(dto)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Yoga Session")
                .hasFieldOrPropertyWithValue("minutes", 60L);
        }
    }

    @Nested
    @DisplayName("Collection Assertions Comparison")
    class CollectionAssertions {

        @Test
        @DisplayName("List content and size checks")
        void collectionAssertions() {
            List<Activity> activities = Arrays.asList(
                new Activity("Running", 30, LocalDate.of(2024, 1, 10)),
                new Activity("Swimming", 45, LocalDate.of(2024, 1, 11)),
                new Activity("Cycling", 60, LocalDate.of(2024, 1, 12))
            );
            
            // JUnit
            assertNotNull(activities);
            assertEquals(3, activities.size());
            assertFalse(activities.isEmpty());
            assertTrue(activities.stream().anyMatch(a -> a.getName().equals("Running")));

            // Hamcrest
            MatcherAssert.assertThat(activities, notNullValue());
            MatcherAssert.assertThat(activities, hasSize(3));
            MatcherAssert.assertThat(activities, not(empty()));
            MatcherAssert.assertThat(activities, hasItem(hasProperty("name", is("Running"))));
            MatcherAssert.assertThat(activities, everyItem(hasProperty("minutes", greaterThan(0L))));

            // AssertJ
            Assertions.assertThat(activities)
                .isNotNull()
                .hasSize(3)
                .isNotEmpty()
                .extracting("name")
                .containsExactly("Running", "Swimming", "Cycling");
                
            Assertions.assertThat(activities)
                .extracting("minutes")
                .allSatisfy(minutes -> Assertions.assertThat((Long) minutes).isGreaterThan(0L))
                .containsExactly(30L, 45L, 60L);
        }

        @Test
        @DisplayName("Checking size and content at the same time")
        void sizeAndContentChecks() {
            List<Activity> activities = Arrays.asList(
                    new Activity("Running", 30, LocalDate.of(2024, 1, 10)),
                    new Activity("Swimming", 45, LocalDate.of(2024, 1, 11)),
                    new Activity("Cycling", 60, LocalDate.of(2024, 1, 12))
//                  ,new Activity("Cycling2", 60, LocalDate.of(2024, 1, 12))
            );

            // list contains 3 items and those are "Running", "Swimming", "Cycling"
//            assertEquals(3, activities.size());
            assertTrue(activities.stream().map(Activity::getName).toList()
                    .containsAll(Arrays.asList("Running", "Swimming", "Cycling")));

            // list contains 3 items and those are "Running", "Swimming", "Cycling"
            MatcherAssert.assertThat(activities.stream().map(Activity::getName).toList(),
                    containsInAnyOrder("Running", "Swimming", "Cycling"));

            // list contains 3 items and those are "Running", "Swimming", "Cycling"
            Assertions.assertThat(activities)
                    .isNotNull()
                    .isNotEmpty()
                    .extracting("name")
                    .containsExactlyInAnyOrder("Running", "Swimming", "Cycling");
        }
    }

    @Nested
    @DisplayName("Exception Assertions Comparison")
    class ExceptionAssertions {

        @Test
        @DisplayName("Exception throwing and message checks")
        void exceptionAssertions() {
            // JUnit
            IllegalArgumentException junitException = assertThrows(
                IllegalArgumentException.class,
                () -> new Activity("", 30, LocalDate.of(2024, 1, 10))
            );
            assertTrue(junitException.getMessage().contains("name cannot be null or empty"));
            
            // Hamcrest - requires more boilerplate :(
            IllegalArgumentException hamcrestException = null;
            try {
                new Activity("", 30, LocalDate.of(2024, 1, 10));
                fail("Expected exception was not thrown");
            } catch (IllegalArgumentException e) {
                hamcrestException = e;
            }
            MatcherAssert.assertThat(hamcrestException, notNullValue());
            MatcherAssert.assertThat(hamcrestException.getMessage(), containsString("name cannot be null or empty"));
            
            // AssertJ
            Assertions.assertThatThrownBy(() -> new Activity("", 30, LocalDate.of(2024, 1, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be null or empty")
                .hasMessage("Activity name cannot be null or empty");
        }
    }
}
