package com.jabaddon.learning.java_spring_testing.app.infra.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityService;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityController.class)
class ActivityControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ActivityService activityService;

    @Test
    @DisplayName("Should return empty list when no activities exist")
    void shouldReturnEmptyListWhenNoActivitiesExist() throws Exception {
        when(activityService.getAllActivities()).thenReturn(List.of());

        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(activityService).getAllActivities();
    }

    @Test
    @DisplayName("Should return all activities when they exist")
    void shouldReturnAllActivitiesWhenTheyExist() throws Exception {
        ActivityDTO activity = new ActivityDTO(1L, "Running", 30, LocalDate.now());
        when(activityService.getAllActivities()).thenReturn(List.of(activity));

        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Running")))
                .andExpect(jsonPath("$[0].minutes", is(30)))
                .andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())));

        verify(activityService).getAllActivities();
    }

    @Test
    @DisplayName("Should return matching activities when filtering by name")
    void shouldReturnMatchingActivitiesWhenFilteringByName() throws Exception {
        ActivityDTO activity = new ActivityDTO(1L, "Running", 30, LocalDate.now());
        when(activityService.searchActivitiesByName("run")).thenReturn(List.of(activity));

        mockMvc.perform(get("/api/activities?name=run"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Running")));

        verify(activityService).searchActivitiesByName("run");
    }

    @Test
    @DisplayName("Should return activity when valid ID is provided")
    void shouldReturnActivityWhenValidIdIsProvided() throws Exception {
        ActivityDTO activity = new ActivityDTO(1L, "Cycling", 60, LocalDate.now());
        when(activityService.getActivityById(1L)).thenReturn(activity);

        mockMvc.perform(get("/api/activities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Cycling")))
                .andExpect(jsonPath("$.minutes", is(60)));

        verify(activityService).getActivityById(1L);
    }

    @Test
    @DisplayName("Should create and return activity when valid data is provided")
    void shouldCreateAndReturnActivityWhenValidDataIsProvided() throws Exception {
        NewActivityDTO newActivity = new NewActivityDTO("Swimming", "45m", LocalDate.now());
        ActivityDTO createdActivity = new ActivityDTO(1L, "Swimming", 45, LocalDate.now());
        
        when(activityService.createActivity(any(NewActivityDTO.class))).thenReturn(createdActivity);

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivity)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Swimming")))
                .andExpect(jsonPath("$.minutes", is(45)))
                .andExpect(jsonPath("$.date", is(LocalDate.now().toString())));

        verify(activityService).createActivity(any(NewActivityDTO.class));
    }

    @Test
    @DisplayName("Should update and return activity when valid data is provided")
    void shouldUpdateAndReturnActivityWhenValidDataIsProvided() throws Exception {
        NewActivityDTO updatedActivity = new NewActivityDTO("Updated", "60m", LocalDate.now());
        ActivityDTO returnedActivity = new ActivityDTO(1L, "Updated", 60, LocalDate.now());
        
        when(activityService.updateActivity(eq(1L), any(NewActivityDTO.class))).thenReturn(returnedActivity);

        mockMvc.perform(put("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.minutes", is(60)));

        verify(activityService).updateActivity(eq(1L), any(NewActivityDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent activity")
    void shouldReturn404WhenUpdatingNonExistentActivity() throws Exception {
        NewActivityDTO updatedActivity = new NewActivityDTO("Updated", "60m", LocalDate.now());
        
        when(activityService.updateActivity(eq(999L), any(NewActivityDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/activities/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isNotFound());

        verify(activityService).updateActivity(eq(999L), any(NewActivityDTO.class));
    }

    @Test
    @DisplayName("Should delete activity and return 204")
    void shouldDeleteActivityAndReturn204() throws Exception {
        doNothing().when(activityService).deleteActivity(1L);

        mockMvc.perform(delete("/api/activities/1"))
                .andExpect(status().isNoContent());

        verify(activityService).deleteActivity(1L);
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() throws Exception {
        when(activityService.getActivityById(999L)).thenThrow(new RuntimeException("Activity not found"));

        mockMvc.perform(get("/api/activities/999"))
                .andExpect(status().is5xxServerError());

        verify(activityService).getActivityById(999L);
    }
}