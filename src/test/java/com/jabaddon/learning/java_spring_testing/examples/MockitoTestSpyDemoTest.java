package com.jabaddon.learning.java_spring_testing.examples;

import com.jabaddon.learning.java_spring_testing.app.application.ActivityService;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MockitoTestSpyDemoTest implements ActivityDomainRepository {
    private List<Activity> updatedActivities = new ArrayList<>();

    @Test
    @DisplayName("should create test spy with self shunt")
    void shouldCreateTestSpyWithSelfShunt() {
        // Arrange
        ActivityService service = new ActivityService(this, null);

        // Act
        service.updateActivity(11L, new NewActivityDTO("Demo", "1h", LocalDate.now()));

        // Assert
        assertThat(this.updatedActivities.size(), is(1));
        Activity updatedActivity = this.updatedActivities.get(0);
        assertThat(updatedActivity.getName(), is("Demo"));
        assertThat(updatedActivity.getMinutes(), is(60L));
        assertThat(updatedActivity.getDate(), is(equalTo(LocalDate.now())));
    }

    @Test
    @DisplayName("should create test spy for arguments")
    void shouldCreateTestSpyForArguments() {
        // Arrange
        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        ActivityDomainRepository repoMock = mock();
        NotificationDomainService notificationDomainServiceDummy = mock();
        ActivityService service = new ActivityService(repoMock, notificationDomainServiceDummy);

        // Stub: simulate findById works but update fails
        Activity activity = new Activity("123", 0L, LocalDate.now());
        when(repoMock.findById(eq(11L))).thenReturn(Optional.of(activity));

        // Act
        service.updateActivity(11L, new NewActivityDTO("Demo", "1h", LocalDate.now()));

        // Assert
        verify(repoMock, times(1)).findById(eq(11L));
        verify(repoMock, times(1)).update(activityCaptor.capture());
        Activity capturedActivity = activityCaptor.getValue();
        assertThat(capturedActivity, is(sameInstance(activity)));
        assertThat(capturedActivity.getName(), is("Demo"));
        assertThat(capturedActivity.getMinutes(), is(60L));
        assertThat(capturedActivity.getDate(), is(equalTo(LocalDate.now())));
    }

    @Override
    public Optional<Activity> findById(Long id) {
        return Optional.of(new Activity("123", 0L, LocalDate.now()));
    }

    @Override
    public List<Activity> findAll() {
        return List.of();
    }

    @Override
    public List<Activity> findByNameContainingIgnoreCase(String name) {
        return List.of();
    }

    @Override
    public Long save(Activity activity) {
        return 0L;
    }

    @Override
    public void update(Activity activity) {
        this.updatedActivities.add(activity);
    }

    @Override
    public void deleteById(Long id) {

    }
}
