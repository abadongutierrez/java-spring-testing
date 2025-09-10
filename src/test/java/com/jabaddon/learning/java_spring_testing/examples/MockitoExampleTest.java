package com.jabaddon.learning.java_spring_testing.examples;

import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityService;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.utils.TimeTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito Usage Examples")
class MockitoExampleTest {

    @Mock
    private NotificationDomainService notificationDomainService;

    @Mock
    private ActivityDomainRepository activityDomainRepository;

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test (optional with @ExtendWith(MockitoExtension.class))
        reset(notificationDomainService, activityDomainRepository);
    }

    @Test
    @DisplayName("Basic mocking - when/thenReturn")
    void shouldDemonstrateBasicMocking() {
        // Arrange
        Activity expectedActivity = new Activity("Running", 30, LocalDate.now());
        when(activityDomainRepository.findById(1L)).thenReturn(Optional.of(expectedActivity));

        // Act
        ActivityDTO actualActivity = activityService.getActivityById(1L);

        // Assert
        assertThat(actualActivity.minutes(), is(equalTo(30L)));
    }

    @Test
    @DisplayName("Verify method calls - verify()")
    void shouldDemonstrateVerifyMethodCalls() {
        // Arrange
        Activity expectedActivity = new Activity("Running", 30, LocalDate.now());
        expectedActivity.setId(1L);
        when(activityDomainRepository.save(any(Activity.class))).thenReturn(1L);
        when(activityDomainRepository.findById(1L)).thenReturn(Optional.of(expectedActivity));

        // Act
        NewActivityDTO newActivityDTO = new NewActivityDTO("Running", "30m", LocalDate.now());
        ActivityDTO actualActivity = activityService.createActivity(newActivityDTO);

        // Assert - verify interactions
        verify(activityDomainRepository, times(1)).save(any(Activity.class));
        verify(activityDomainRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Argument matchers - any(), eq(), anyString()")
    void shouldDemonstrateArgumentMatchers() {
        // Arrange
        Activity existingActivity = new Activity("Old Name", 30L, LocalDate.now());
        existingActivity.setId(1L);
        NewActivityDTO updateData = new NewActivityDTO("New Name", "60m", LocalDate.now());

        when(activityDomainRepository.findById(anyLong())).thenReturn(Optional.of(existingActivity));
        doNothing().when(activityDomainRepository).update(any(Activity.class));

        // Act
        ActivityDTO result = activityService.updateActivity(1L, updateData);

        // Assert
        verify(activityDomainRepository).findById(eq(1L));
        verify(activityDomainRepository).update(argThat(activity ->
                activity.getName().equals("New Name") && activity.getMinutes() == 60L));
        assertThat(result.name(), is(equalTo("New Name")));

    }

    @Test
    @DisplayName("Argument captor - capturing method arguments")
    void shouldDemonstrateArgumentCaptor() {
        // Arrange
        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);

        Activity existingActivity = new Activity("Old Name", 30L, LocalDate.now());
        existingActivity.setId(1L);
        NewActivityDTO updateData = new NewActivityDTO("New Name", "60m", LocalDate.now());

        when(activityDomainRepository.findById(anyLong())).thenReturn(Optional.of(existingActivity));
        doNothing().when(activityDomainRepository).update(activityCaptor.capture());

        // Act
        activityService.updateActivity(1L, updateData);

        // Assert
        verify(activityDomainRepository).findById(eq(1L));
        assertThat(activityCaptor.getValue().getMinutes(), is(60L));
        assertThat(activityCaptor.getValue().getName(), is("New Name"));
    }

    @Test
    @DisplayName("Static method mocking with MockedStatic")
    void shouldDemonstrateStaticMethodMocking() {
        // Arrange
        try (MockedStatic<TimeTranslator> mockedTimeTranslator = mockStatic(TimeTranslator.class)) {
            mockedTimeTranslator.when(() -> TimeTranslator.toMinutes(anyString())).thenThrow(new IllegalArgumentException("Invalid time format"));

            // Act and Assert
            assertThrows(IllegalArgumentException.class, () ->
                activityService.createActivity(new NewActivityDTO("Running", "20m", LocalDate.now()))
            );
        }
    }

    @Test
    @DisplayName("Answer with custom logic")
    void shouldDemonstrateAnswerUsage() {
        // Arrange
        when(activityDomainRepository.save(any(Activity.class))).thenAnswer(invocation -> {
            Activity activity = invocation.getArgument(0);
            // Simulate setting an ID after saving
            return activity.getMinutes();
        });

        Activity savedActivity = new Activity("Running", 30, LocalDate.now());
        // just for demo purposes
        when(activityDomainRepository.findById(eq(savedActivity.getMinutes()))).thenReturn(Optional.of(savedActivity));

        // Act
        NewActivityDTO newActivityDTO = new NewActivityDTO("Running", "30m", LocalDate.now());
        ActivityDTO result = activityService.createActivity(newActivityDTO);

        // Assert
        assertThat(result.name(), is(equalTo("Running")));
        assertThat(result.minutes(), is(equalTo(30L)));
    }
}
