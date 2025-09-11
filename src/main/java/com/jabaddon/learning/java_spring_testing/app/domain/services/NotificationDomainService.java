package com.jabaddon.learning.java_spring_testing.app.domain.services;

import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;

public interface NotificationDomainService {
    void sendActivityDeletedNotification(Activity activity);
}