package com.mycompany.myapp.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

/**
 * Unit tests for the {@link UserNotActivatedException}.
 */
class UserNotActivatedExceptionTest {

    @Test
    void shouldCreateUserNotActivatedException() {
        // When
        UserNotActivatedException exception = new UserNotActivatedException("User not activated");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(AuthenticationException.class);
        assertThat(exception.getMessage()).isEqualTo("User not activated");
    }

    @Test
    void shouldCreateUserNotActivatedExceptionWithDefaultMessage() {
        // When
        UserNotActivatedException exception = new UserNotActivatedException("User " + "test@example.com" + " was not activated");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User test@example.com was not activated");
    }
}
