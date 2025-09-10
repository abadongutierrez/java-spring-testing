package com.jabaddon.learning.java_spring_testing.app.infra.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabaddon.learning.java_spring_testing.TestcontainersConfiguration;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories.ActivityRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.testconfig.TestEmailConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, TestEmailConfiguration.class})
@ActiveProfiles("test")
@Transactional
class ActivityControllerSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private NotificationDomainService notificationDomainService;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        reset(notificationDomainService);
    }

    @Test
    @DisplayName("Should return empty list when no activities exist")
    void shouldReturnEmptyListWhenNoActivitiesExist() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return all activities when they exist")
    void shouldReturnAllActivitiesWhenTheyExist() throws Exception {
        // Create test activity
        NewActivityDTO newActivity = new NewActivityDTO("Running", "30m", LocalDate.now());
        createActivityAndReturn(newActivity);

        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Running")))
                .andExpect(jsonPath("$[0].minutes", is(30)))
                .andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())));
    }

    @Test
    @DisplayName("Should return matching activities when filtering by name")
    void shouldReturnMatchingActivitiesWhenFilteringByName() throws Exception {
        // Create test activities
        NewActivityDTO running = new NewActivityDTO("Running", "30m", LocalDate.now());
        NewActivityDTO walking = new NewActivityDTO("Walking", "45m", LocalDate.now());
        createActivityAndReturn(running);
        createActivityAndReturn(walking);

        mockMvc.perform(get("/api/activities?name=run"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Running")));
    }

    @Test
    @DisplayName("Should return activity when valid ID is provided")
    void shouldReturnActivityWhenValidIdIsProvided() throws Exception {
        // Create test activity
        NewActivityDTO newActivity = new NewActivityDTO("Cycling", "60m", LocalDate.now());
        ActivityDTO created = createActivityAndReturn(newActivity);

        mockMvc.perform(get("/api/activities/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(created.id().intValue())))
                .andExpect(jsonPath("$.name", is("Cycling")))
                .andExpect(jsonPath("$.minutes", is(60)));
    }

    @Test
    @DisplayName("Should return 404 when activity ID is not found")
    void shouldReturn404WhenActivityIdIsNotFound() throws Exception {
        mockMvc.perform(get("/api/activities/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create and return activity when valid data is provided")
    void shouldCreateAndReturnActivityWhenValidDataIsProvided() throws Exception {
        NewActivityDTO newActivity = new NewActivityDTO("Swimming", "45m", LocalDate.now());

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivity)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Swimming")))
                .andExpect(jsonPath("$.minutes", is(45)))
                .andExpect(jsonPath("$.date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Should return 400 when activity date is in the future")
    void shouldReturn400WhenActivityDateIsInFuture() throws Exception {
        NewActivityDTO futureActivity = new NewActivityDTO("Future Activity", "30m", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureActivity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update and return activity when valid data is provided")
    void shouldUpdateAndReturnActivityWhenValidDataIsProvided() throws Exception {
        // Create test activity
        NewActivityDTO originalActivity = new NewActivityDTO("Original", "30m", LocalDate.now());
        ActivityDTO created = createActivityAndReturn(originalActivity);

        // Update activity
        NewActivityDTO updatedActivity = new NewActivityDTO("Updated", "60m", LocalDate.now());

        mockMvc.perform(put("/api/activities/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(created.id().intValue())))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.minutes", is(60)));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent activity")
    void shouldReturn404WhenUpdatingNonExistentActivity() throws Exception {
        NewActivityDTO updatedActivity = new NewActivityDTO("Updated", "60m", LocalDate.now());

        mockMvc.perform(put("/api/activities/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete activity and return 204 with notification sent")
    void shouldDeleteActivityAndReturn204WithNotificationSent() throws Exception {
        // Create test activity
        NewActivityDTO newActivity = new NewActivityDTO("To Delete", "30m", LocalDate.now());
        ActivityDTO created = createActivityAndReturn(newActivity);

        mockMvc.perform(delete("/api/activities/" + created.id()))
                .andExpect(status().isNoContent());

        // Verify activity is deleted
        mockMvc.perform(get("/api/activities/" + created.id()))
                .andExpect(status().isNotFound());

        // Verify email notification was sent
        verify(notificationDomainService, times(1)).sendActivityDeletedNotification(any());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent activity")
    void shouldReturn404WhenDeletingNonExistentActivity() throws Exception {
        mockMvc.perform(delete("/api/activities/999"))
                .andExpect(status().isNotFound());

        // Verify no emails sent when deletion fails
        verify(notificationDomainService, never()).sendActivityDeletedNotification(any());
    }

    @Test
    @DisplayName("Should send multiple email notifications when deleting multiple activities")
    void shouldSendMultipleEmailNotificationsWhenDeletingMultipleActivities() throws Exception {
        // Create multiple test activities
        NewActivityDTO activity1 = new NewActivityDTO("Activity 1", "30m", LocalDate.now());
        NewActivityDTO activity2 = new NewActivityDTO("Activity 2", "45m", LocalDate.now());
        NewActivityDTO activity3 = new NewActivityDTO("Activity 3", "60m", LocalDate.now());
        
        ActivityDTO created1 = createActivityAndReturn(activity1);
        ActivityDTO created2 = createActivityAndReturn(activity2);
        ActivityDTO created3 = createActivityAndReturn(activity3);

        // Delete first activity
        mockMvc.perform(delete("/api/activities/" + created1.id()))
                .andExpect(status().isNoContent());

        // Delete second activity
        mockMvc.perform(delete("/api/activities/" + created2.id()))
                .andExpect(status().isNoContent());

        // Delete third activity
        mockMvc.perform(delete("/api/activities/" + created3.id()))
                .andExpect(status().isNoContent());

        // Verify three email notifications were sent
        verify(notificationDomainService, times(3)).sendActivityDeletedNotification(any());
    }

    @Test
    @DisplayName("Should return 400 when activity name is empty")
    void shouldReturn400WhenActivityNameIsEmpty() throws Exception {
        NewActivityDTO invalidActivity = new NewActivityDTO("", "30m", LocalDate.now());

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidActivity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when activity minutes are negative")
    void shouldReturn400WhenActivityMinutesAreNegative() throws Exception {
        NewActivityDTO invalidActivity = new NewActivityDTO("Test", "-30m", LocalDate.now());

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidActivity)))
                .andExpect(status().isBadRequest());
    }

    private ActivityDTO createActivityAndReturn(NewActivityDTO newActivity) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivity)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, ActivityDTO.class);
    }
}