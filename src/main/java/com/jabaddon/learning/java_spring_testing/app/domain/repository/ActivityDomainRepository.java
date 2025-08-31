package com.jabaddon.learning.java_spring_testing.app.domain.repository;

import com.jabaddon.learning.java_spring_testing.app.domain.model.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityDomainRepository {
    Optional<Activity> findById(Long id);
    List<Activity> findAll();
    List<Activity> findByNameContainingIgnoreCase(String name);
    Long save(Activity activity);
    void update(Activity activity);
    void deleteById(Long id);
}
