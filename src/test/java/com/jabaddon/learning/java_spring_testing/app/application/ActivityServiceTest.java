package com.jabaddon.learning.java_spring_testing.app.application;

import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.utils.TimeTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService Unit Tests")
class ActivityServiceTest {

    @Mock
    private ActivityDomainRepository activityRepository;

    @Mock
    private NotificationDomainService notificationService;

    @InjectMocks
    private ActivityService activityService;

    private Activity testActivity;
    private NewActivityDTO newActivityDTO;

    @BeforeEach
    void setUp() {
        testActivity = new Activity("Running", 30L, LocalDate.of(2024, 1, 1));
        testActivity.setId(1L);
        newActivityDTO = new NewActivityDTO("Swimming", "45m", LocalDate.of(2024, 1, 2));
    }

    @Nested
    @DisplayName("Get All Activities")
    class GetAllActivitiesTests {

        @Test
        @DisplayName("Should return all activities as DTOs")
        void shouldReturnAllActivitiesAsDTOs() {
            Activity activity1 = new Activity("Running", 30L, LocalDate.of(2024, 1, 1));
            activity1.setId(1L);
            Activity activity2 = new Activity("Swimming", 45L, LocalDate.of(2024, 1, 2));
            activity2.setId(2L);
            
            List<Activity> activities = Arrays.asList(activity1, activity2);
            when(activityRepository.findAll()).thenReturn(activities);

            List<ActivityDTO> result = activityService.getAllActivities();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).name()).isEqualTo("Running");
            assertThat(result.get(0).minutes()).isEqualTo(30L);
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(1).name()).isEqualTo("Swimming");
            assertThat(result.get(1).minutes()).isEqualTo(45L);
            
            verify(activityRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no activities exist")
        void shouldReturnEmptyListWhenNoActivitiesExist() {
            when(activityRepository.findAll()).thenReturn(Arrays.asList());

            List<ActivityDTO> result = activityService.getAllActivities();

            assertThat(result).isEmpty();
            verify(activityRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Search Activities by Name")
    class SearchActivitiesByNameTests {

        @Test
        @DisplayName("Should return activities matching name pattern")
        void shouldReturnActivitiesMatchingNamePattern() {
            List<Activity> foundActivities = Arrays.asList(testActivity);
            when(activityRepository.findByNameContainingIgnoreCase("run")).thenReturn(foundActivities);

            List<ActivityDTO> result = activityService.searchActivitiesByName("run");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Running");
            verify(activityRepository).findByNameContainingIgnoreCase("run");
        }

        @Test
        @DisplayName("Should return empty list when no activities match")
        void shouldReturnEmptyListWhenNoActivitiesMatch() {
            when(activityRepository.findByNameContainingIgnoreCase("nonexistent")).thenReturn(Arrays.asList());

            List<ActivityDTO> result = activityService.searchActivitiesByName("nonexistent");

            assertThat(result).isEmpty();
            verify(activityRepository).findByNameContainingIgnoreCase("nonexistent");
        }
    }

    @Nested
    @DisplayName("Get Activity by ID")
    class GetActivityByIdTests {

        @Test
        @DisplayName("Should return activity when found")
        void shouldReturnActivityWhenFound() {
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

            ActivityDTO result = activityService.getActivityById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Running");
            assertThat(result.minutes()).isEqualTo(30L);
            assertThat(result.date()).isEqualTo(LocalDate.of(2024, 1, 1));
            verify(activityRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when activity not found")
        void shouldThrowExceptionWhenActivityNotFound() {
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityService.getActivityById(999L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Activity not found");

            verify(activityRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("Create Activity")
    class CreateActivityTests {

        @Test
        @DisplayName("Should create and return new activity")
        void shouldCreateAndReturnNewActivity() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("45m")).thenReturn(45L);
                
                Activity savedActivity = new Activity("Swimming", 45L, LocalDate.of(2024, 1, 2));
                savedActivity.setId(2L);
                
                when(activityRepository.save(any(Activity.class))).thenReturn(2L);
                when(activityRepository.findById(2L)).thenReturn(Optional.of(savedActivity));

                ActivityDTO result = activityService.createActivity(newActivityDTO);

                assertThat(result.id()).isEqualTo(2L);
                assertThat(result.name()).isEqualTo("Swimming");
                assertThat(result.minutes()).isEqualTo(45L);
                assertThat(result.date()).isEqualTo(LocalDate.of(2024, 1, 2));
                
                verify(activityRepository).save(argThat(activity -> 
                    activity.getName().equals("Swimming") && 
                    activity.getMinutes() == 45L &&
                    activity.getDate().equals(LocalDate.of(2024, 1, 2))
                ));
                verify(activityRepository).findById(2L);
            }
        }

        @Test
        @DisplayName("Should throw IllegalStateException when saved activity cannot be retrieved")
        void shouldThrowExceptionWhenSavedActivityCannotBeRetrieved() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("45m")).thenReturn(45L);
                
                when(activityRepository.save(any(Activity.class))).thenReturn(2L);
                when(activityRepository.findById(2L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> activityService.createActivity(newActivityDTO))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Activity could not be created");

                verify(activityRepository).save(any(Activity.class));
                verify(activityRepository).findById(2L);
            }
        }
    }

    @Nested
    @DisplayName("Update Activity")
    class UpdateActivityTests {

        @Test
        @DisplayName("Should update existing activity successfully")
        void shouldUpdateExistingActivitySuccessfully() {
            try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
                mockedTimeTranslator.when(() -> TimeTranslator.toMinutes("60m")).thenReturn(60L);
                
                NewActivityDTO updateData = new NewActivityDTO("Updated Running", "60m", LocalDate.of(2024, 1, 3));
                when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
                doNothing().when(activityRepository).update(any(Activity.class));

                ActivityDTO result = activityService.updateActivity(1L, updateData);

                assertThat(result.id()).isEqualTo(1L);
                assertThat(result.name()).isEqualTo("Updated Running");
                assertThat(result.minutes()).isEqualTo(60L);
                assertThat(result.date()).isEqualTo(LocalDate.of(2024, 1, 3));
                
                ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
                verify(activityRepository).update(activityCaptor.capture());
                
                Activity updatedActivity = activityCaptor.getValue();
                assertThat(updatedActivity.getName()).isEqualTo("Updated Running");
                assertThat(updatedActivity.getMinutes()).isEqualTo(60L);
                assertThat(updatedActivity.getDate()).isEqualTo(LocalDate.of(2024, 1, 3));
            }
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when activity to update not found")
        void shouldThrowExceptionWhenActivityToUpdateNotFound() {
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityService.updateActivity(999L, newActivityDTO))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Activity not found");

            verify(activityRepository).findById(999L);
            verify(activityRepository, never()).update(any(Activity.class));
        }
    }

    @Nested
    @DisplayName("Delete Activity")
    class DeleteActivityTests {

        @Test
        @DisplayName("Should delete activity and send notification")
        void shouldDeleteActivityAndSendNotification() {
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            doNothing().when(activityRepository).deleteById(1L);
            doNothing().when(notificationService).sendActivityDeletedNotification(testActivity);

            activityService.deleteActivity(1L);

            verify(activityRepository).findById(1L);
            verify(activityRepository).deleteById(1L);
            verify(notificationService).sendActivityDeletedNotification(testActivity);
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when activity to delete not found")
        void shouldThrowExceptionWhenActivityToDeleteNotFound() {
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityService.deleteActivity(999L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Activity not found");

            verify(activityRepository).findById(999L);
            verify(activityRepository, never()).deleteById(anyLong());
            verify(notificationService, never()).sendActivityDeletedNotification(any(Activity.class));
        }

        @Test
        @DisplayName("Should find activity before deletion to ensure notification has correct data")
        void shouldFindActivityBeforeDeletionForNotification() {
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            doNothing().when(activityRepository).deleteById(1L);
            doNothing().when(notificationService).sendActivityDeletedNotification(testActivity);

            activityService.deleteActivity(1L);

            // Verify the order of operations: find first, then delete, then notify
            var inOrder = inOrder(activityRepository, notificationService);
            inOrder.verify(activityRepository).findById(1L);
            inOrder.verify(activityRepository).deleteById(1L);
            inOrder.verify(notificationService).sendActivityDeletedNotification(testActivity);
        }
    }
}