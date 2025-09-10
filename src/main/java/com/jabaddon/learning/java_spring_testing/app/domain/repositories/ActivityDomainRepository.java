package com.jabaddon.learning.java_spring_testing.app.domain.repositories;

import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityDomainRepository {
    Optional<Activity> findById(Long id);
    List<Activity> findAll();
    List<Activity> findByNameContainingIgnoreCase(String name);
    Long save(Activity activity);
    void update(Activity activity);
    void deleteById(Long id);
}
