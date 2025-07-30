package com.mycompany.myapp;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link TicketTrackerApp}.
 */
class TicketTrackerAppTest {

    @Test
    void shouldCreateTicketTrackerApp() {
        // This test is mainly to verify the class can be loaded and has correct annotations
        assertThat(TicketTrackerApp.class).isNotNull();
    }

    @Test
    void shouldHaveCorrectAnnotations() {
        // Given
        Class<TicketTrackerApp> appClass = TicketTrackerApp.class;

        // Then
        assertThat(appClass.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class))
            .isTrue();
        assertThat(appClass.isAnnotationPresent(org.springframework.boot.context.properties.EnableConfigurationProperties.class))
            .isTrue();
    }
}
