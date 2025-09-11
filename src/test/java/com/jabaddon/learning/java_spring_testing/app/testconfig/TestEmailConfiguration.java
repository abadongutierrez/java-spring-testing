package com.jabaddon.learning.java_spring_testing.app.testconfig;

import com.jabaddon.learning.java_spring_testing.app.domain.services.NotificationDomainService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestEmailConfiguration {
    
    @Bean
    @Primary
    public NotificationDomainService emailNotificationServiceMock() {
        return Mockito.mock(NotificationDomainService.class);
    }
}