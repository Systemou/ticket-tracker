package com.mycompany.myapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link UsernameAlreadyUsedException}.
 */
class UsernameAlreadyUsedExceptionTest {

    @Test
    void shouldCreateUsernameAlreadyUsedException() {
        // When
        UsernameAlreadyUsedException exception = new UsernameAlreadyUsedException();

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Login name already used!");
    }
}
