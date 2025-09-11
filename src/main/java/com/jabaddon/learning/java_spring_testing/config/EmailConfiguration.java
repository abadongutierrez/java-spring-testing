package com.jabaddon.learning.java_spring_testing.config;

import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import com.jabaddon.learning.java_spring_testing.app.infra.email.EmailNotificationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EmailConfiguration {
    
    @Bean
    public NotificationDomainService emailNotificationService(@Value("${app.email.from:noreply@example.com}") String fromEmail) {
        return new EmailNotificationServiceImpl(fromEmail);
    }
}