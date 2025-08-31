package com.jabaddon.learning.java_spring_testing.app.infra.persistence.repositories;

import com.jabaddon.learning.java_spring_testing.app.infra.persistence.entities.ActivityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityJpaEntity, Long> {
    List<ActivityJpaEntity> findByNameContainingIgnoreCase(String name);
}