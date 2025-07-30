package com.mycompany.myapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link EmailAlreadyUsedException}.
 */
class EmailAlreadyUsedExceptionTest {

    @Test
    void shouldCreateEmailAlreadyUsedException() {
        // Given
        String email = "test@example.com";

        // When
        EmailAlreadyUsedException exception = new EmailAlreadyUsedException();

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Email is already in use!");
    }
}
