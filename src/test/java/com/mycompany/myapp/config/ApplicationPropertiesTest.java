package com.mycompany.myapp.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ApplicationProperties}.
 */
class ApplicationPropertiesTest {

    private ApplicationProperties applicationProperties;

    @BeforeEach
    void setUp() {
        applicationProperties = new ApplicationProperties();
    }

    @Test
    void shouldCreateApplicationProperties() {
        // Then
        assertThat(applicationProperties).isNotNull();
    }

    @Test
    void shouldHaveDefaultValues() {
        // Then
        assertThat(applicationProperties.getLiquibase()).isNotNull();
        assertThat(applicationProperties.getLiquibase().getAsyncStart()).isTrue();
    }
}
