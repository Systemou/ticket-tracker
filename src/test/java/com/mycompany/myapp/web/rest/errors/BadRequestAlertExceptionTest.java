package com.mycompany.myapp.web.rest.errors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Unit tests for the {@link BadRequestAlertException}.
 */
class BadRequestAlertExceptionTest {

    @Test
    void shouldCreateBadRequestAlertException() {
        // Given
        String defaultMessage = "A new entity cannot already have an ID";
        String entityName = "testEntity";
        String errorKey = "idexists";

        // When
        BadRequestAlertException exception = new BadRequestAlertException(defaultMessage, entityName, errorKey);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getEntityName()).isEqualTo(entityName);
        assertThat(exception.getErrorKey()).isEqualTo(errorKey);
        assertThat(exception.getProblemDetailWithCause()).isNotNull();
    }

    @Test
    void shouldCreateBadRequestAlertExceptionWithCustomMessage() {
        // Given
        String customMessage = "Custom error message";
        String entityName = "user";
        String errorKey = "customerror";

        // When
        BadRequestAlertException exception = new BadRequestAlertException(customMessage, entityName, errorKey);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getEntityName()).isEqualTo(entityName);
        assertThat(exception.getErrorKey()).isEqualTo(errorKey);
    }
}
