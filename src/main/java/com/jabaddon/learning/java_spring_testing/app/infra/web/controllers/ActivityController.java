package com.jabaddon.learning.java_spring_testing.app.infra.web.controllers;

import com.jabaddon.learning.java_spring_testing.app.application.ActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.NewActivityDTO;
import com.jabaddon.learning.java_spring_testing.app.application.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    
    private final ActivityService activityService;
    
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getActivities(@RequestParam(required = false) String name) {
        List<ActivityDTO> activities;
        if (name != null && !name.isEmpty()) {
            activities = activityService.searchActivitiesByName(name);
        } else {
            activities = activityService.getAllActivities();
        }
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        ActivityDTO activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }
    
    @PostMapping("")
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody NewActivityDTO activityDTO) {
        ActivityDTO createdActivity = activityService.createActivity(activityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody NewActivityDTO activityDTO) {
        ActivityDTO updatedActivity = activityService.updateActivity(id, activityDTO);
        if (updatedActivity != null) {
            return ResponseEntity.ok(updatedActivity);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
