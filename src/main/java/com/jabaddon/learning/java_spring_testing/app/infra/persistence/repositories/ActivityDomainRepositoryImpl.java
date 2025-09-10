package com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories;

import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import com.jabaddon.learning.java_spring_testing.app.domain.repositories.ActivityDomainRepository;
import com.jabaddon.learning.java_spring_testing.app.infra.persistence.entities.ActivityJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ActivityDomainRepositoryImpl implements ActivityDomainRepository {
    private final ActivityRepository activityRepository;

    public ActivityDomainRepositoryImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Optional<Activity> findById(Long id) {
        Optional<ActivityJpaEntity> entityOptional = activityRepository.findById(id);
        return entityOptional.map(this::toModel);
    }

    @Override
    public List<Activity> findAll() {
        List<ActivityJpaEntity> entities = activityRepository.findAll();
        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> findByNameContainingIgnoreCase(String name) {
        List<ActivityJpaEntity> entities = activityRepository.findByNameContainingIgnoreCase(name);
        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Long save(Activity model) {
        ActivityJpaEntity newEntity = toEntity(model);
        ActivityJpaEntity savedEntity = activityRepository.save(newEntity);
        return savedEntity.getId();
    }

    @Override
    public void update(Activity model) {
        Optional<ActivityJpaEntity> existingEntityOptional = activityRepository.findById(model.getId());
        if (existingEntityOptional.isPresent()) {
            ActivityJpaEntity existingEntity = existingEntityOptional.get();
            existingEntity.setName(model.getName());
            existingEntity.setMinutes(model.getMinutes());
            existingEntity.setDate(model.getDate());
            activityRepository.save(existingEntity);
        } else {
            throw new NoSuchElementException("Activity with ID " + model.getId() + " not found");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Activity with ID " + id + " not found");
        }
    }

    private ActivityJpaEntity toEntity(Activity model) {
        return new ActivityJpaEntity(
            model.getName(),
            model.getMinutes(),
            model.getDate()
        );
    }

    private Activity toModel(ActivityJpaEntity entity) {
        Activity activity = new Activity(
            entity.getName(),
            entity.getMinutes(),
            entity.getDate()
        );
        activity.setId(entity.getId());
        return activity;
    }
}
