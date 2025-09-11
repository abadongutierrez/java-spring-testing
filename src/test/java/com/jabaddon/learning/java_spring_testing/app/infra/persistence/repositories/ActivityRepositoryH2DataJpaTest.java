package com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories;

import com.jabaddon.learning.java_spring_testing.app.infra.persistence.entities.ActivityJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
class ActivityRepositoryH2DataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActivityRepository activityRepository;

    private ActivityJpaEntity runningActivity;
    private ActivityJpaEntity walkingActivity;
    private ActivityJpaEntity cyclingActivity;

    @BeforeEach
    void setUp() {
        runningActivity = new ActivityJpaEntity("Running", 30L, LocalDate.now());
        walkingActivity = new ActivityJpaEntity("Walking", 45L, LocalDate.now().minusDays(1));
        cyclingActivity = new ActivityJpaEntity("Cycling", 60L, LocalDate.now().minusDays(2));
    }

    @Test
    @DisplayName("Should save and find activity by ID")
    void shouldSaveAndFindActivityById() {
        ActivityJpaEntity savedActivity = activityRepository.save(runningActivity);
        entityManager.flush();

        Optional<ActivityJpaEntity> foundActivity = activityRepository.findById(savedActivity.getId());

        assertThat(foundActivity).isPresent();
        assertThat(foundActivity.get().getName()).isEqualTo("Running");
        assertThat(foundActivity.get().getMinutes()).isEqualTo(30L);
        assertThat(foundActivity.get().getDate()).isEqualTo(LocalDate.now());
        assertThat(foundActivity.get().getCreatedAt()).isNotNull();
        assertThat(foundActivity.get().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find all activities")
    void shouldFindAllActivities() {
        activityRepository.save(runningActivity);
        activityRepository.save(walkingActivity);
        activityRepository.save(cyclingActivity);
        entityManager.flush();

        List<ActivityJpaEntity> activities = activityRepository.findAll();

        assertThat(activities).hasSize(3);
        assertThat(activities).extracting(ActivityJpaEntity::getName)
                .containsExactlyInAnyOrder("Running", "Walking", "Cycling");
    }

    @Test
    @DisplayName("Should find activities by name containing ignore case")
    void shouldFindActivitiesByNameContainingIgnoreCase() {
        activityRepository.save(runningActivity);
        activityRepository.save(walkingActivity);
        activityRepository.save(cyclingActivity);
        entityManager.flush();

        List<ActivityJpaEntity> activities = activityRepository.findByNameContainingIgnoreCase("run");

        assertThat(activities).hasSize(1);
        assertThat(activities.getFirst().getName()).isEqualTo("Running");
    }

    @Test
    @DisplayName("Should find activities by partial name case insensitive")
    void shouldFindActivitiesByPartialNameCaseInsensitive() {
        activityRepository.save(runningActivity);
        activityRepository.save(walkingActivity);
        activityRepository.save(cyclingActivity);
        entityManager.flush();

        List<ActivityJpaEntity> activitiesWithIng = activityRepository.findByNameContainingIgnoreCase("ING");

        assertThat(activitiesWithIng).hasSize(3);
        assertThat(activitiesWithIng).extracting(ActivityJpaEntity::getName)
                .containsExactlyInAnyOrder("Running", "Walking", "Cycling");
    }

    @Test
    @DisplayName("Should return empty list when no activities match name search")
    void shouldReturnEmptyListWhenNoActivitiesMatchNameSearch() {
        activityRepository.save(runningActivity);
        activityRepository.save(walkingActivity);
        entityManager.flush();

        List<ActivityJpaEntity> activities = activityRepository.findByNameContainingIgnoreCase("swimming");

        assertThat(activities).isEmpty();
    }

    @Test
    @DisplayName("Should delete activity by ID")
    void shouldDeleteActivityById() {
        ActivityJpaEntity savedActivity = activityRepository.save(runningActivity);
        entityManager.flush();

        activityRepository.deleteById(savedActivity.getId());
        entityManager.flush();

        Optional<ActivityJpaEntity> deletedActivity = activityRepository.findById(savedActivity.getId());
        assertThat(deletedActivity).isEmpty();
    }

    @Test
    @DisplayName("Should update activity")
    void shouldUpdateActivity() {
        ActivityJpaEntity savedActivity = activityRepository.save(runningActivity);
        entityManager.flush();
        entityManager.clear();

        savedActivity.setName("Updated Running");
        savedActivity.setMinutes(45L);
        ActivityJpaEntity updatedActivity = activityRepository.save(savedActivity);
        entityManager.flush();

        Optional<ActivityJpaEntity> foundActivity = activityRepository.findById(updatedActivity.getId());
        assertThat(foundActivity).isPresent();
        assertThat(foundActivity.get().getName()).isEqualTo("Updated Running");
        assertThat(foundActivity.get().getMinutes()).isEqualTo(45L);
        assertThat(foundActivity.get().getUpdatedAt()).isAfter(foundActivity.get().getCreatedAt());
    }

    @Test
    @DisplayName("Should count total activities")
    void shouldCountTotalActivities() {
        activityRepository.save(runningActivity);
        activityRepository.save(walkingActivity);
        entityManager.flush();

        long count = activityRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should check if activity exists by ID")
    void shouldCheckIfActivityExistsById() {
        ActivityJpaEntity savedActivity = activityRepository.save(runningActivity);
        entityManager.flush();

        boolean exists = activityRepository.existsById(savedActivity.getId());
        boolean notExists = activityRepository.existsById(999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should return empty result when finding non-existent activity")
    void shouldReturnEmptyResultWhenFindingNonExistentActivity() {
        Optional<ActivityJpaEntity> foundActivity = activityRepository.findById(999L);

        assertThat(foundActivity).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty name search")
    void shouldHandleEmptyNameSearch() {
        activityRepository.save(runningActivity);
        activityRepository.save(walkingActivity);
        entityManager.flush();

        List<ActivityJpaEntity> activities = activityRepository.findByNameContainingIgnoreCase("");

        assertThat(activities).hasSize(2);
    }
}