package com.jabaddon.learning.java_spring_testing.app.application;

import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.utils.TimeTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ActivityService {
    
    private final ActivityDomainRepository activityRepository;
    private final NotificationDomainService notificationService;
    
    @Autowired
    public ActivityService(ActivityDomainRepository activityRepository, 
                          NotificationDomainService emailNotificationService) {
        this.activityRepository = activityRepository;
        this.notificationService = emailNotificationService;
    }
    
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ActivityDTO> searchActivitiesByName(String name) {
        return activityRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .toList();
    }

    public ActivityDTO getActivityById(Long id) {
        return activityRepository.findById(id).map(this::toDTO).orElseThrow(() -> new NoSuchElementException("Activity not found"));
    }
    
    public ActivityDTO createActivity(NewActivityDTO activity) {
        Long id = activityRepository.save(toModel(activity));
        Optional<Activity> newActivity = activityRepository.findById(id);
        // Activity should be there, if not something went wrong
        if (newActivity.isEmpty()) {
            throw new IllegalStateException("Activity could not be created");
        }
        return toDTO(newActivity.get());
    }
    
    public ActivityDTO updateActivity(Long id, NewActivityDTO activityDetails) {
        Optional<Activity> optionalActivity = activityRepository.findById(id);
        if (optionalActivity.isEmpty()) {
            throw new NoSuchElementException("Activity not found");
        }
        Activity activity = optionalActivity.get();
        long minutes = TimeTranslator.toMinutes(activityDetails.time());
        activity.update(activityDetails.name(), minutes, activityDetails.date());
        activityRepository.update(activity);
        return toDTO(activity);
    }

    public void deleteActivity(Long id) {
        // Fetch activity before deletion to send notification
        Optional<Activity> optionalActivity = activityRepository.findById(id);
        if (optionalActivity.isEmpty()) {
            throw new NoSuchElementException("Activity not found");
        }
        
        Activity activity = optionalActivity.get();
        activityRepository.deleteById(id);
        
        // Send email notification after successful deletion
        notificationService.sendActivityDeletedNotification(activity);
    }

    private ActivityDTO toDTO(Activity activity) {
        return new ActivityDTO(
                activity.getId(),
                activity.getName(),
                activity.getMinutes(),
                activity.getDate()
        );
    }

    // factory
    private Activity toModel(NewActivityDTO newActivityDTO) {
        return new Activity(
                newActivityDTO.name(),
                TimeTranslator.toMinutes(newActivityDTO.time()),
                newActivityDTO.date()
        );
    }
}