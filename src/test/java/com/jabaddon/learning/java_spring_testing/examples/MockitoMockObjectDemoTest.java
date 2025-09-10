package com.jabaddon.learning.java_spring_testing.examples;

import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityService;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MockitoMockObjectDemoTest {
    @Test
    @DisplayName("should create a mock object")
    void shouldCreateResponderStub() {
        // Arrange
        ActivityDomainRepository repositoryResponderStub = mock(ActivityDomainRepository.class);
        NotificationDomainService notificationDomainServiceDummy = mock(NotificationDomainService.class);
        ActivityService service = new ActivityService(repositoryResponderStub, notificationDomainServiceDummy);

        Activity activity = new Activity("Morning Run", 42L, LocalDate.now());
        activity.setId(1L);
        // Stub: simulate valid save and findById
        when(repositoryResponderStub.save(any(Activity.class))).thenReturn(activity.getId());
        when(repositoryResponderStub.findById(anyLong())).thenReturn(Optional.of(activity));

        // Act
        ActivityDTO created = service.createActivity(
                new NewActivityDTO(activity.getName(), "42m", LocalDate.now()));

        // Assert
        assertThat(created.id(), is(activity.getId()));
        assertThat(created.name(), is(activity.getName()));
        assertThat(created.minutes(), is(activity.getMinutes()));
        assertThat(created.date(), is(equalTo(LocalDate.now())));

        // Verify interactions
        verify(repositoryResponderStub, times(1)).save(any(Activity.class));
        verify(repositoryResponderStub, times(1)).findById(anyLong());
    }
}
