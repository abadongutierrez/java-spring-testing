package com.jabaddon.learning.java_spring_testing.app.infra.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabaddon.learning.java_spring_testing.TestcontainersConfiguration;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories.ActivityRepository;
import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.testconfig.TestEmailConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.jabaddon.learning.java_spring_testing.app.testutils.ActivityMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.ArgumentCaptor;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, TestEmailConfiguration.class})
@ActiveProfiles("test")
@Transactional
@DisplayName("Activity Controller Integration Tests")
class ActivityControllerSpringBootNestedTest {

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

    @Nested
    @DisplayName("Complete Activity Lifecycle")
    class CompleteActivityLifecycle {

        @Nested
        @DisplayName("When Empty Database")
        class EmptyDatabaseVerification {

            @Test
            @DisplayName("Should return 0 activities when getting all activities")
            void shouldStartWithEmptyDatabase() throws Exception {
                mockMvc.perform(get("/api/activities"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));
            }

            @Nested
            @DisplayName("When some activities are created")
            class WhenActivitiesExist {
                private ActivityDTO runningActivityCreated;

                @BeforeEach
                void before() throws Exception {
                    NewActivityDTO running = new NewActivityDTO("Running", "30m", LocalDate.now());
                    NewActivityDTO walking = new NewActivityDTO("Walking", "45m", LocalDate.now().minusDays(2));
                    runningActivityCreated = createActivityAndReturn(running);
                    createActivityAndReturn(walking);
                }

                @Test
                @DisplayName("Should return all activities created")
                void shouldReturnAllActivitiesWhenTheyExist() throws Exception {
                    mockMvc.perform(get("/api/activities"))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$", hasSize(2)))
                            .andExpect(jsonPath("$[*].name", containsInAnyOrder("Running", "Walking")))
                            .andExpect(jsonPath("$[*].minutes", containsInAnyOrder(30, 45)))
                            .andExpect(jsonPath("$[*].date",
                                    containsInAnyOrder(LocalDate.now().toString(),
                                            LocalDate.now().minusDays(2).toString())));
                }

                @Nested
                @DisplayName("When filtering by name")
                class WhenFilteringByName {
                    @Test
                    @DisplayName("Should return matching activities")
                    void shouldReturnMatchingActivitiesWhenFilteringByName() throws Exception {
                        mockMvc.perform(get("/api/activities?name=run"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name", is("Running")));
                    }
                }

                @Nested
                @DisplayName("When no activities match filter")
                class WhenNoActivitiesMatchFilter {
                    @Test
                    @DisplayName("Should return empty list")
                    void shouldReturnEmptyListWhenNoActivitiesMatchFilter() throws Exception {
                        mockMvc.perform(get("/api/activities?name=swim"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(0)));
                    }
                }

                @Nested
                @DisplayName("When updating an activity")
                class WhenUpdatingAnActivity {
                    ResultActions performedUpdate;

                    @BeforeEach
                    void before() throws Exception {
                        NewActivityDTO updatedActivity = new NewActivityDTO("Updated Running", "60m", LocalDate.now());

                        performedUpdate = mockMvc.perform(put("/api/activities/" + runningActivityCreated.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedActivity)));
                    }

                    @Test
                    @DisplayName("Should update and return the updated activity")
                    void shouldUpdateAndReturnActivityWhenValidDataIsProvided() throws Exception {
                        performedUpdate
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id", is(runningActivityCreated.id().intValue())))
                                .andExpect(jsonPath("$.name", is("Updated Running")))
                                .andExpect(jsonPath("$.minutes", is(60)));
                    }

                    @Nested
                    @DisplayName("When deleting an activity")
                    class WhenDeletingAnActivity {
                        ResultActions performDeleteResult;
                        ArgumentCaptor<Activity> notificationArgumentCapture;

                        @BeforeEach
                        void before() throws Exception {
                            // Setup argument captor to capture the argument passed to the notification service
                            notificationArgumentCapture = ArgumentCaptor.forClass(Activity.class);

                            performDeleteResult = mockMvc.perform(delete(
                                    "/api/activities/" + runningActivityCreated.id()));
                        }

                        @Test
                        @DisplayName("Should delete and return 204")
                        void shouldReturn204WhenActivityIsDeleted() throws Exception {
                            performDeleteResult.andExpect(status().isNoContent());
                        }

                        @Test
                        @DisplayName("Should notify about the deletion")
                        void shouldNotifyAboutDeletion() {
                            verify(notificationDomainService, times(1))
                                    .sendActivityDeletedNotification(notificationArgumentCapture.capture());

                            Activity capturedActivity = notificationArgumentCapture.getValue();

                            assertThat(capturedActivity, allOf(
                                    hasId(runningActivityCreated.id()),
                                    hasName("Updated Running"),
                                    hasMinutes(60L)));

                            assertThat(capturedActivity, isActivityWith(
                                    runningActivityCreated.id(),
                                    "Updated Running",
                                    60L));
                        }

                        @Test
                        @DisplayName("Should return 404 when fetching the deleted activity")
                        void shouldDeleteActivityAndReturn204WithNotificationSent() throws Exception {
                            mockMvc.perform(get("/api/activities/" + runningActivityCreated.id()))
                                    .andExpect(status().isNotFound());
                        }
                    }
                }
            }
        }
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
