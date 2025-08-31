package com.jabaddon.learning.java_spring_testing.app.infra.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabaddon.learning.java_spring_testing.TestcontainersConfiguration;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
    }

    @Test
    void testGetAllActivities_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllActivities_ShouldReturnAllActivities() throws Exception {
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
    void testGetActivitiesByName_ShouldReturnMatchingActivities() throws Exception {
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
    void testGetActivityById_ShouldReturnActivity() throws Exception {
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
    void testGetActivityById_ShouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/activities/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateActivity_ShouldCreateAndReturnActivity() throws Exception {
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
    void testCreateActivity_ShouldReturn400WhenDateIsInFuture() throws Exception {
        NewActivityDTO futureActivity = new NewActivityDTO("Future Activity", "30m", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureActivity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateActivity_ShouldUpdateAndReturnActivity() throws Exception {
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
    void testUpdateActivity_ShouldReturn404WhenNotFound() throws Exception {
        NewActivityDTO updatedActivity = new NewActivityDTO("Updated", "60m", LocalDate.now());

        mockMvc.perform(put("/api/activities/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteActivity_ShouldDeleteAndReturn204() throws Exception {
        // Create test activity
        NewActivityDTO newActivity = new NewActivityDTO("To Delete", "30m", LocalDate.now());
        ActivityDTO created = createActivityAndReturn(newActivity);

        mockMvc.perform(delete("/api/activities/" + created.id()))
                .andExpect(status().isNoContent());

        // Verify activity is deleted
        mockMvc.perform(get("/api/activities/" + created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteActivity_ShouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/activities/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateActivity_ShouldReturn400WhenNameIsEmpty() throws Exception {
        NewActivityDTO invalidActivity = new NewActivityDTO("", "30m", LocalDate.now());

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidActivity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateActivity_ShouldReturn400WhenMinutesAreNegative() throws Exception {
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