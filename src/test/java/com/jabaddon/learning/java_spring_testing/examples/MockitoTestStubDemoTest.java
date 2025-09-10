package com.jabaddon.learning.java_spring_testing.examples;

import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityService;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories.ActivityDomainRepositoryImpl;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories.ActivityRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Mockito Test Stub Demo Test")
public class MockitoTestStubDemoTest {

    public static final ConstraintViolationException CONSTRAINT_VIOLATION_EXCEPTION =
            new ConstraintViolationException("DB error", null, "constraint_name");

    @Test
    @DisplayName("should create a responder stub")
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
    }

    @Test
    @DisplayName("should create a saboteur stub")
    void shouldCreateSaboteurStub() {
        // Arrange
        ActivityDomainRepository repoMock = mock();
        NotificationDomainService notificationDomainServiceDummy = mock();
        ActivityService service = new ActivityService(repoMock, notificationDomainServiceDummy);
        Activity activityDummy = mock();

        // Stub: simulate findById works but update fails
        when(repoMock.findById(eq(11L))).thenReturn(Optional.of(activityDummy));
        doThrow(CONSTRAINT_VIOLATION_EXCEPTION).when(repoMock).update(any(Activity.class));

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () ->
            service.updateActivity(11L, new NewActivityDTO("Demo", "1h", LocalDate.now())));
    }
}
