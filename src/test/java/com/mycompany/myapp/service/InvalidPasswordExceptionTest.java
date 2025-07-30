package com.mycompany.myapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link InvalidPasswordException}.
 */
class InvalidPasswordExceptionTest {

    @Test
    void shouldCreateInvalidPasswordException() {
        // When
        InvalidPasswordException exception = new InvalidPasswordException();

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Incorrect password");
    }
}
