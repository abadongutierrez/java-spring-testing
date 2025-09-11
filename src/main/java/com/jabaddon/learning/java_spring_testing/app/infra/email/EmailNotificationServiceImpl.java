package com.jabaddon.learning.java_spring_testing.app.infra.email;

import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotificationServiceImpl implements NotificationDomainService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);
    
    private String fromEmail;

    public EmailNotificationServiceImpl(String fromEmail) {
        this.fromEmail = fromEmail;
    }
    
    @Override
    public void sendActivityDeletedNotification(Activity activity) {
        logger.info("Activity deletion notification sent for activity: {} (ID: {}) to email: {}",
            activity.getName(), activity.getId(), fromEmail);
    }
}