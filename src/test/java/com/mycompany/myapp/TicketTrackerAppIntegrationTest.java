package com.mycompany.myapp;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for the {@link TicketTrackerApp}.
 * This test verifies that the Spring application context can be loaded properly.
 */
@SpringBootTest(classes = TicketTrackerApp.class)
@ActiveProfiles("test")
class TicketTrackerAppIntegrationTest {

    @Test
    void shouldLoadApplicationContext() {
        // This test verifies that the Spring application context can be loaded
        // The test will fail if there are any configuration issues
        assertThat(true).isTrue();
    }
}
