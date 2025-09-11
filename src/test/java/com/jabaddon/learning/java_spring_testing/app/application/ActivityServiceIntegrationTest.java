package com.jabaddon.learning.java_spring_testing.app.application;

import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories.ActivityRepository;
import com.jabaddon.learning.java_spring_testing.utils.TimeTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
@Transactional
@DisplayName("ActivityService Integration Tests")
class ActivityServiceIntegrationTest {

    @Container
    // Using a static container to ensure it's shared across all tests for efficiency
    private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRepository activityRepository;

    @MockitoBean
    private NotificationDomainService notificationService;

    private NewActivityDTO newActivityDTO;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        reset(notificationService);
        newActivityDTO = new NewActivityDTO("Swimming", "45m", LocalDate.of(2024, 1, 2));
    }

    @Nested
    @DisplayName("Get All Activities")
    class GetAllActivitiesTests {

        @Test
        @DisplayName("Should return all activities from database")
        void shouldReturnAllActivitiesFromDatabase() {
            createTestActivities();

            List<ActivityDTO> result = activityService.getAllActivities();

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ActivityDTO::name)
                    .containsExactlyInAnyOrder("Running", "Swimming");
        }

        @Test
        @DisplayName("Should return empty list when no activities exist in database")
        void shouldReturnEmptyListWhenNoActivitiesExistInDatabase() {
            List<ActivityDTO> result = activityService.getAllActivities();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search Activities by Name")
    class SearchActivitiesByNameTests {

        @Test
        @DisplayName("Should return activities matching name pattern from database")
        void shouldReturnActivitiesMatchingNamePatternFromDatabase() {
            createTestActivities();

            List<ActivityDTO> result = activityService.searchActivitiesByName("run");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Running");
        }

        @Test
        @DisplayName("Should return case-insensitive matches from database")
        void shouldReturnCaseInsensitiveMatchesFromDatabase() {
            createTestActivities();

            List<ActivityDTO> result = activityService.searchActivitiesByName("RUN");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Running");
        }

        @Test
        @DisplayName("Should return empty list when no activities match in database")
        void shouldReturnEmptyListWhenNoActivitiesMatchInDatabase() {
            createTestActivities();

            List<ActivityDTO> result = activityService.searchActivitiesByName("nonexistent");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get Activity by ID")
    class GetActivityByIdTests {

        @Test
        @DisplayName("Should return activity when found in database")
        void shouldReturnActivityWhenFoundInDatabase() {
            ActivityDTO created = createSingleTestActivity();

            ActivityDTO result = activityService.getActivityById(created.id());

            assertThat(result.id()).isEqualTo(created.id());
            assertThat(result.name()).isEqualTo("Running");
            assertThat(result.minutes()).isEqualTo(30L);
            assertThat(result.date()).isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when activity not found in database")
        void shouldThrowExceptionWhenActivityNotFoundInDatabase() {
            assertThatThrownBy(() -> activityService.getActivityById(999L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Activity not found");
        }
    }

    @Nested
    @DisplayName("Create Activity")
    class CreateActivityTests {

        @Test
        @DisplayName("Should persist new activity to database and return it")
        void shouldPersistNewActivityToDatabaseAndReturnIt() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("45m")).thenReturn(45L);

                ActivityDTO result = activityService.createActivity(newActivityDTO);

                assertThat(result.id()).isNotNull();
                assertThat(result.name()).isEqualTo("Swimming");
                assertThat(result.minutes()).isEqualTo(45L);
                assertThat(result.date()).isEqualTo(LocalDate.of(2024, 1, 2));

                // Verify activity is persisted in database
                assertThat(activityRepository.findAll()).hasSize(1);
                assertThat(activityRepository.findById(result.id())).isPresent();
            }
        }

        @Test
        @DisplayName("Should handle domain validation during creation")
        void shouldHandleDomainValidationDuringCreation() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("30m")).thenReturn(30L);
                
                NewActivityDTO invalidActivity = new NewActivityDTO("", "30m", LocalDate.of(2024, 1, 1));

                assertThatThrownBy(() -> activityService.createActivity(invalidActivity))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Activity name cannot be null or empty");

                // Verify nothing was persisted
                assertThat(activityRepository.findAll()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Update Activity")
    class UpdateActivityTests {

        @Test
        @DisplayName("Should update existing activity in database")
        void shouldUpdateExistingActivityInDatabase() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                NewActivityDTO newActivity = new NewActivityDTO("Running", "30m", LocalDate.of(2024, 1, 1));
                ActivityDTO created = activityService.createActivity(newActivity);

                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("60m")).thenReturn(60L);
                NewActivityDTO updateData = new NewActivityDTO("Updated Running", "60m", LocalDate.of(2024, 1, 3));

                ActivityDTO result = activityService.updateActivity(created.id(), updateData);

                assertThat(result.id()).isEqualTo(created.id());
                assertThat(result.name()).isEqualTo("Updated Running");
                assertThat(result.minutes()).isEqualTo(60L);
                assertThat(result.date()).isEqualTo(LocalDate.of(2024, 1, 3));

                // Verify activity is updated in database
                ActivityDTO fromDb = activityService.getActivityById(created.id());
                assertThat(fromDb.name()).isEqualTo("Updated Running");
                assertThat(fromDb.minutes()).isEqualTo(60L);
            }
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when activity to update not found in database")
        void shouldThrowExceptionWhenActivityToUpdateNotFoundInDatabase() {
            assertThatThrownBy(() -> activityService.updateActivity(999L, newActivityDTO))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Activity not found");
        }

        @Test
        @DisplayName("Should handle domain validation during update")
        void shouldHandleDomainValidationDuringUpdate() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                NewActivityDTO newActivity = new NewActivityDTO("Running", "30m", LocalDate.of(2024, 1, 1));
                ActivityDTO created = activityService.createActivity(newActivity);

                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("30m")).thenReturn(30L);
                NewActivityDTO invalidUpdate = new NewActivityDTO("", "30m", LocalDate.of(2024, 1, 1));

                assertThatThrownBy(() -> activityService.updateActivity(created.id(), invalidUpdate))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Activity name cannot be null or empty");

                // Verify original activity is unchanged
                ActivityDTO unchanged = activityService.getActivityById(created.id());
                assertThat(unchanged.name()).isEqualTo("Running");
            }
        }
    }

    @Nested
    @DisplayName("Delete Activity")
    class DeleteActivityTests {

        @Test
        @DisplayName("Should delete activity from database and send notification")
        void shouldDeleteActivityFromDatabaseAndSendNotification() {
            ActivityDTO created = createSingleTestActivity();

            activityService.deleteActivity(created.id());

            // Verify activity is deleted from database
            assertThat(activityRepository.findById(created.id())).isEmpty();
            assertThat(activityRepository.findAll()).isEmpty();

            // Verify notification was sent
            verify(notificationService, times(1)).sendActivityDeletedNotification(any(Activity.class));
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when activity to delete not found in database")
        void shouldThrowExceptionWhenActivityToDeleteNotFoundInDatabase() {
            assertThatThrownBy(() -> activityService.deleteActivity(999L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Activity not found");

            // Verify no notification sent
            verify(notificationService, never()).sendActivityDeletedNotification(any(Activity.class));
        }

        @Test
        @DisplayName("Should send notification with correct activity data before deletion")
        void shouldSendNotificationWithCorrectActivityDataBeforeDeletion() {
            ActivityDTO created = createSingleTestActivity();

            activityService.deleteActivity(created.id());

            // Verify notification was sent with the activity data
            verify(notificationService).sendActivityDeletedNotification(argThat(activity ->
                    activity.getName().equals("Running") &&
                    activity.getMinutes() == 30L &&
                    activity.getDate().equals(LocalDate.of(2024, 1, 1))
            ));
        }
    }

    @Nested
    @DisplayName("Database Persistence Integration")
    class DatabasePersistenceIntegrationTests {

        @Test
        @DisplayName("Should handle concurrent operations correctly")
        void shouldHandleConcurrentOperationsCorrectly() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes(anyString())).thenReturn(30L, 45L, 60L);

                // Create multiple activities
                NewActivityDTO activity1 = new NewActivityDTO("Activity1", "30m", LocalDate.of(2024, 1, 1));
                NewActivityDTO activity2 = new NewActivityDTO("Activity2", "45m", LocalDate.of(2024, 1, 2));
                NewActivityDTO activity3 = new NewActivityDTO("Activity3", "60m", LocalDate.of(2024, 1, 3));

                ActivityDTO created1 = activityService.createActivity(activity1);
                ActivityDTO created2 = activityService.createActivity(activity2);
                ActivityDTO created3 = activityService.createActivity(activity3);

                // Verify all are in database
                List<ActivityDTO> allActivities = activityService.getAllActivities();
                assertThat(allActivities).hasSize(3);

                // Delete one
                activityService.deleteActivity(created2.id());

                // Verify correct state
                allActivities = activityService.getAllActivities();
                assertThat(allActivities).hasSize(2);
                assertThat(allActivities)
                        .extracting(ActivityDTO::name)
                        .containsExactlyInAnyOrder("Activity1", "Activity3");
            }
        }

        @Test
        @DisplayName("Should maintain data integrity across operations")
        void shouldMaintainDataIntegrityAcrossOperations() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("30m")).thenReturn(30L);
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("60m")).thenReturn(60L);

                // Create activity
                NewActivityDTO newActivity = new NewActivityDTO("Original", "30m", LocalDate.of(2024, 1, 1));
                ActivityDTO created = activityService.createActivity(newActivity);

                // Update activity
                NewActivityDTO updateData = new NewActivityDTO("Updated", "60m", LocalDate.of(2024, 1, 2));
                ActivityDTO updated = activityService.updateActivity(created.id(), updateData);

                // Verify update persisted correctly
                ActivityDTO fromDb = activityService.getActivityById(created.id());
                assertThat(fromDb.id()).isEqualTo(created.id()).isEqualTo(updated.id());
                assertThat(fromDb.name()).isEqualTo("Updated");
                assertThat(fromDb.minutes()).isEqualTo(60L);
                assertThat(fromDb.date()).isEqualTo(LocalDate.of(2024, 1, 2));
            }
        }
    }

    private ActivityDTO createSingleTestActivity() {
        try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
            mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("30m")).thenReturn(30L);
            
            NewActivityDTO newActivity = new NewActivityDTO("Running", "30m", LocalDate.of(2024, 1, 1));
            return activityService.createActivity(newActivity);
        }
    }

    private void createTestActivities() {
        try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
            mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("30m")).thenReturn(30L);
            mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("45m")).thenReturn(45L);

            NewActivityDTO activity1 = new NewActivityDTO("Running", "30m", LocalDate.of(2024, 1, 1));
            NewActivityDTO activity2 = new NewActivityDTO("Swimming", "45m", LocalDate.of(2024, 1, 2));
            
            activityService.createActivity(activity1);
            activityService.createActivity(activity2);
        }
    }
}
